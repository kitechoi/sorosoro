package com.sorosoro.common.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI sorosoroOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SOROSORO API")
                        .description("소로소로(SOROSORO) 개인 재봉 기록 서비스 API")
                        .version("v1"));
    }
}
