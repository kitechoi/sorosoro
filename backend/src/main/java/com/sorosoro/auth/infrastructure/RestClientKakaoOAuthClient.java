package com.sorosoro.auth.infrastructure;

import com.sorosoro.auth.config.KakaoOAuthProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class RestClientKakaoOAuthClient implements KakaoOAuthClient {

    private final KakaoOAuthProperties properties;
    private final RestClient restClient;

    public RestClientKakaoOAuthClient(KakaoOAuthProperties properties, RestClient.Builder restClientBuilder) {
        this.properties = properties;
        this.restClient = restClientBuilder.build();
    }

    @Override
    public KakaoUserInfo getUserInfo(String authorizationCode, String redirectUri) {
        try {
            KakaoTokenResponse tokenResponse = requestToken(authorizationCode, redirectUri);
            if (tokenResponse == null || tokenResponse.accessToken() == null || tokenResponse.accessToken().isBlank()) {
                throw new KakaoOAuthException("Kakao token response is empty", null);
            }
            KakaoUserResponse userResponse = requestUserInfo(tokenResponse.accessToken());
            if (userResponse == null || userResponse.id() == null) {
                throw new KakaoOAuthException("Kakao user response is empty", null);
            }
            return userResponse.toUserInfo();
        } catch (RuntimeException exception) {
            throw new KakaoOAuthException("Kakao OAuth request failed", exception);
        }
    }

    private KakaoTokenResponse requestToken(String authorizationCode, String redirectUri) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", properties.clientId());
        body.add("redirect_uri", redirectUri);
        body.add("code", authorizationCode);
        if (properties.clientSecret() != null && !properties.clientSecret().isBlank()) {
            body.add("client_secret", properties.clientSecret());
        }

        return restClient.post()
                .uri(properties.tokenUri())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .body(KakaoTokenResponse.class);
    }

    private KakaoUserResponse requestUserInfo(String kakaoAccessToken) {
        return restClient.get()
                .uri(properties.userInfoUri())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                .retrieve()
                .body(KakaoUserResponse.class);
    }

    private record KakaoTokenResponse(@JsonProperty("access_token") String accessToken) {
    }

    private record KakaoUserResponse(Long id, Map<String, Object> properties) {

        KakaoUserInfo toUserInfo() {
            String nickname = properties == null ? null : (String) properties.get("nickname");
            String profileImageUrl = properties == null ? null : (String) properties.get("profile_image");
            return new KakaoUserInfo(String.valueOf(id), nickname, profileImageUrl);
        }
    }
}
