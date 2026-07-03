package com.sorosoro.auth.dto;

public record KakaoLoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserSummaryResponse user
) {
}
