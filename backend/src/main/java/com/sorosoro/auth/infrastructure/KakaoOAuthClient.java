package com.sorosoro.auth.infrastructure;

public interface KakaoOAuthClient {

    KakaoUserInfo getUserInfo(String authorizationCode, String redirectUri);
}
