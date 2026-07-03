package com.sorosoro.user.dto;

import com.sorosoro.user.domain.User;
import com.sorosoro.user.domain.UserStatus;
import java.time.LocalDateTime;

public record UserMeResponse(
        Long id,
        String nickname,
        String profileImageUrl,
        UserStatus status,
        LocalDateTime createdAt
) {

    public static UserMeResponse from(User user) {
        return new UserMeResponse(
                user.getId(),
                user.getNickname(),
                user.getProfileImageUrl(),
                user.getStatus(),
                user.getCreatedAt()
        );
    }
}
