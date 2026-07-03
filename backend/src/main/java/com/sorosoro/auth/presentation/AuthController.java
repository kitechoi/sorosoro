package com.sorosoro.auth.presentation;

import com.sorosoro.auth.application.AuthService;
import com.sorosoro.auth.dto.AuthTokenResponse;
import com.sorosoro.auth.dto.KakaoLoginRequest;
import com.sorosoro.auth.dto.KakaoLoginResponse;
import com.sorosoro.auth.dto.LogoutRequest;
import com.sorosoro.auth.dto.MessageResponse;
import com.sorosoro.auth.dto.ReissueRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/kakao/login")
    public KakaoLoginResponse loginWithKakao(@Valid @RequestBody KakaoLoginRequest request) {
        return authService.loginWithKakao(request);
    }

    @PostMapping("/reissue")
    public AuthTokenResponse reissue(@Valid @RequestBody ReissueRequest request) {
        return authService.reissue(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@Valid @RequestBody LogoutRequest request) {
        authService.logout(request);
        return ResponseEntity.ok(new MessageResponse("로그아웃되었습니다."));
    }
}
