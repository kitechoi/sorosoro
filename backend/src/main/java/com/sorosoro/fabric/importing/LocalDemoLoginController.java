package com.sorosoro.fabric.importing;

import com.sorosoro.auth.domain.JwtTokenProvider;
import com.sorosoro.user.domain.*;
import com.sorosoro.user.repository.UserRepository;

import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/** Explicit opt-in for a local, disposable database; never enabled under prod. */
@RestController
@Profile("local-demo & !prod")
public class LocalDemoLoginController {
    private final UserRepository users;
    private final JwtTokenProvider tokens;

    public LocalDemoLoginController(UserRepository users, JwtTokenProvider tokens) {
        this.users = users;
        this.tokens = tokens;
    }

    @PostMapping("/api/v1/local-demo/login")
    @Transactional
    public Map<String, Object> login() {
        User user =
                users.findByKakaoId("local-receipt-demo")
                        .orElseGet(
                                () ->
                                        users.saveAndFlush(
                                                User.builder()
                                                        .kakaoId("local-receipt-demo")
                                                        .nickname("나의 재봉 기록")
                                                        .role(UserRole.USER)
                                                        .status(UserStatus.ACTIVE)
                                                        .build()));
        return Map.of("accessToken", tokens.generateAccessToken(user));
    }
}
