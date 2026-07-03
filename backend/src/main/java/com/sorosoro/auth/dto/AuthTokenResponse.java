package com.sorosoro.auth.dto;

public record AuthTokenResponse(
        String accessToken,
        String tokenType,
        long expiresIn
) {
}
