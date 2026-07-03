package com.sorosoro.auth.domain;

import com.sorosoro.user.domain.UserRole;

public record TokenClaims(
        Long userId,
        UserRole role,
        TokenType tokenType
) {
}
