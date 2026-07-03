package com.sorosoro.auth.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({JwtProperties.class, KakaoOAuthProperties.class})
public class AuthPropertiesConfig {
}
