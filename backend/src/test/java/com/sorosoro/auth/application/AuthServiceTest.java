package com.sorosoro.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sorosoro.auth.config.JwtProperties;
import com.sorosoro.auth.domain.JwtTokenProvider;
import com.sorosoro.auth.domain.RefreshToken;
import com.sorosoro.auth.dto.KakaoLoginRequest;
import com.sorosoro.auth.dto.KakaoLoginResponse;
import com.sorosoro.auth.dto.LogoutRequest;
import com.sorosoro.auth.dto.ReissueRequest;
import com.sorosoro.auth.infrastructure.KakaoOAuthClient;
import com.sorosoro.auth.infrastructure.KakaoUserInfo;
import com.sorosoro.auth.repository.RefreshTokenRepository;
import com.sorosoro.user.domain.User;
import com.sorosoro.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class AuthServiceTest {

    private KakaoOAuthClient kakaoOAuthClient;
    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private JwtTokenProvider jwtTokenProvider;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        kakaoOAuthClient = Mockito.mock(KakaoOAuthClient.class);
        userRepository = Mockito.mock(UserRepository.class);
        refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        jwtTokenProvider = new JwtTokenProvider(new JwtProperties(
                "test-secret-key-must-be-at-least-32-bytes",
                3600,
                1209600
        ));
        authService = new AuthService(kakaoOAuthClient, jwtTokenProvider, userRepository, refreshTokenRepository);
    }

    @Test
    void loginWithKakaoCreatesUserAndRefreshToken() {
        KakaoUserInfo kakaoUserInfo = new KakaoUserInfo("12345", "soro", "https://profile");
        User savedUser = User.builder()
                .kakaoId(kakaoUserInfo.kakaoId())
                .nickname(kakaoUserInfo.nickname())
                .profileImageUrl(kakaoUserInfo.profileImageUrl())
                .build();
        ReflectionTestUtils.setField(savedUser, "id", 1L);

        when(kakaoOAuthClient.getUserInfo("code", "https://redirect")).thenReturn(kakaoUserInfo);
        when(userRepository.findByKakaoId("12345")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        KakaoLoginResponse response = authService.loginWithKakao(new KakaoLoginRequest("code", "https://redirect"));

        assertThat(response.accessToken()).isNotBlank();
        assertThat(response.refreshToken()).isNotBlank();
        assertThat(response.tokenType()).isEqualTo("Bearer");
        assertThat(response.expiresIn()).isEqualTo(3600);
        assertThat(response.user().id()).isEqualTo(1L);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getToken()).isEqualTo(response.refreshToken());
    }

    @Test
    void reissueReturnsNewAccessToken() {
        User user = User.builder().kakaoId("12345").nickname("soro").build();
        ReflectionTestUtils.setField(user, "id", 1L);
        String refreshTokenValue = jwtTokenProvider.generateRefreshToken(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .user(user)
                .token(refreshTokenValue)
                .expiredAt(LocalDateTime.now().plusDays(1))
                .build();

        when(refreshTokenRepository.findByToken(refreshTokenValue)).thenReturn(Optional.of(refreshToken));

        assertThat(authService.reissue(new ReissueRequest(refreshTokenValue)).accessToken()).isNotBlank();
    }

    @Test
    void logoutDeletesRefreshToken() {
        authService.logout(new LogoutRequest("refresh-token"));

        verify(refreshTokenRepository).deleteByToken("refresh-token");
    }
}
