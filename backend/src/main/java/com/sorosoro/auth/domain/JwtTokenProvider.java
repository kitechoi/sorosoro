package com.sorosoro.auth.domain;

import com.sorosoro.auth.config.JwtProperties;
import com.sorosoro.common.exception.ApiException;
import com.sorosoro.common.exception.ErrorCode;
import com.sorosoro.user.domain.User;
import com.sorosoro.user.domain.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private static final String ROLE_CLAIM = "role";
    private static final String TOKEN_TYPE_CLAIM = "tokenType";

    private final SecretKey secretKey;
    private final long accessTokenExpirationSeconds;
    private final long refreshTokenExpirationSeconds;

    public JwtTokenProvider(JwtProperties properties) {
        this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationSeconds = properties.accessTokenExpirationSeconds();
        this.refreshTokenExpirationSeconds = properties.refreshTokenExpirationSeconds();
    }

    public String generateAccessToken(User user) {
        return generateToken(user, TokenType.ACCESS, accessTokenExpirationSeconds);
    }

    public String generateRefreshToken(User user) {
        return generateToken(user, TokenType.REFRESH, refreshTokenExpirationSeconds);
    }

    public TokenClaims parse(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Long userId = Long.valueOf(claims.getSubject());
            UserRole role = UserRole.valueOf(claims.get(ROLE_CLAIM, String.class));
            TokenType tokenType = TokenType.valueOf(claims.get(TOKEN_TYPE_CLAIM, String.class));
            return new TokenClaims(userId, role, tokenType);
        } catch (RuntimeException exception) {
            throw new ApiException(ErrorCode.INVALID_TOKEN);
        }
    }

    public long getAccessTokenExpirationSeconds() {
        return accessTokenExpirationSeconds;
    }

    public long getRefreshTokenExpirationSeconds() {
        return refreshTokenExpirationSeconds;
    }

    private String generateToken(User user, TokenType tokenType, long expirationSeconds) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(expirationSeconds);
        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim(ROLE_CLAIM, user.getRole().name())
                .claim(TOKEN_TYPE_CLAIM, tokenType.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiresAt))
                .signWith(secretKey)
                .compact();
    }
}
