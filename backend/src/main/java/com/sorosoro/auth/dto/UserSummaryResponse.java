package com.sorosoro.auth.dto;

import com.sorosoro.user.domain.User;

public record UserSummaryResponse(
        Long id,
        String nickname,
        String profileImageUrl
) {

    public static UserSummaryResponse from(User user) {
        return new UserSummaryResponse(user.getId(), user.getNickname(), user.getProfileImageUrl());
    }
}
