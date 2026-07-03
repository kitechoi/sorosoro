package com.sorosoro.auth.infrastructure;

public record KakaoUserInfo(
        String kakaoId,
        String nickname,
        String profileImageUrl
) {
}
