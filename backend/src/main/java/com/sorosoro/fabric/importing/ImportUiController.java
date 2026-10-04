package com.sorosoro.fabric.importing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class ImportUiController {
    private final String clientId;
    private final Environment env;

    public ImportUiController(
            @Value("${kakao.oauth.client-id:}") String clientId, Environment env) {
        this.clientId = clientId;
        this.env = env;
    }

    @GetMapping(value = "/imports/", produces = "text/html")
    public org.springframework.core.io.Resource index() {
        return new org.springframework.core.io.ClassPathResource("static/imports/index.html");
    }

    @GetMapping("/api/v1/import-ui-config")
    public Map<String, Object> config() {
        return Map.of(
                "kakaoClientId",
                clientId,
                "demo",
                env.acceptsProfiles(Profiles.of("local-demo & !prod")));
    }
}
