package com.sorosoro.auth.application;

import com.sorosoro.auth.domain.JwtTokenProvider;
import com.sorosoro.auth.domain.RefreshToken;
import com.sorosoro.auth.domain.TokenClaims;
import com.sorosoro.auth.domain.TokenType;
import com.sorosoro.auth.dto.AuthTokenResponse;
import com.sorosoro.auth.dto.KakaoLoginRequest;
import com.sorosoro.auth.dto.KakaoLoginResponse;
import com.sorosoro.auth.dto.LogoutRequest;
import com.sorosoro.auth.dto.ReissueRequest;
import com.sorosoro.auth.dto.UserSummaryResponse;
import com.sorosoro.auth.infrastructure.KakaoOAuthClient;
import com.sorosoro.auth.infrastructure.KakaoOAuthException;
import com.sorosoro.auth.infrastructure.KakaoUserInfo;
import com.sorosoro.auth.repository.RefreshTokenRepository;
import com.sorosoro.common.exception.ApiException;
import com.sorosoro.common.exception.ErrorCode;
import com.sorosoro.user.domain.User;
import com.sorosoro.user.domain.UserRole;
import com.sorosoro.user.domain.UserStatus;
import com.sorosoro.user.repository.UserRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final KakaoOAuthClient kakaoOAuthClient;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthService(KakaoOAuthClient kakaoOAuthClient, JwtTokenProvider jwtTokenProvider,
                       UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.kakaoOAuthClient = kakaoOAuthClient;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public KakaoLoginResponse loginWithKakao(KakaoLoginRequest request) {
        KakaoUserInfo kakaoUserInfo = getKakaoUserInfo(request);
        User user = userRepository.findByKakaoId(kakaoUserInfo.kakaoId())
                .orElseGet(() -> createUser(kakaoUserInfo));

        if (user.isDeleted()) {
            throw new ApiException(ErrorCode.DELETED_USER);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);
        saveRefreshToken(user, refreshToken);

        return new KakaoLoginResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenProvider.getAccessTokenExpirationSeconds(),
                UserSummaryResponse.from(user)
        );
    }

    @Transactional(readOnly = true)
    public AuthTokenResponse reissue(ReissueRequest request) {
        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_REQUIRED);
        }

        TokenClaims claims = jwtTokenProvider.parse(request.refreshToken());
        if (claims.tokenType() != TokenType.REFRESH) {
            throw new ApiException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .orElseThrow(() -> new ApiException(ErrorCode.INVALID_REFRESH_TOKEN));

        if (refreshToken.isExpired(LocalDateTime.now())) {
            throw new ApiException(ErrorCode.REFRESH_TOKEN_EXPIRED);
        }

        User user = refreshToken.getUser();
        if (user.isDeleted()) {
            throw new ApiException(ErrorCode.DELETED_USER);
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        return new AuthTokenResponse(accessToken, "Bearer", jwtTokenProvider.getAccessTokenExpirationSeconds());
    }

    @Transactional
    public void logout(LogoutRequest request) {
        if (request.refreshToken() == null || request.refreshToken().isBlank()) {
            return;
        }
        refreshTokenRepository.deleteByToken(request.refreshToken());
    }

    private KakaoUserInfo getKakaoUserInfo(KakaoLoginRequest request) {
        try {
            return kakaoOAuthClient.getUserInfo(request.authorizationCode(), request.redirectUri());
        } catch (KakaoOAuthException exception) {
            throw new ApiException(ErrorCode.KAKAO_LOGIN_FAILED);
        }
    }

    private User createUser(KakaoUserInfo kakaoUserInfo) {
        return userRepository.save(User.builder()
                .kakaoId(kakaoUserInfo.kakaoId())
                .nickname(kakaoUserInfo.nickname())
                .profileImageUrl(kakaoUserInfo.profileImageUrl())
                .role(UserRole.USER)
                .status(UserStatus.ACTIVE)
                .build());
    }

    private void saveRefreshToken(User user, String refreshToken) {
        refreshTokenRepository.save(RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiredAt(LocalDateTime.now().plusSeconds(jwtTokenProvider.getRefreshTokenExpirationSeconds()))
                .build());
    }
}
