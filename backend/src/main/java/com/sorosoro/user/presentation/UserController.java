package com.sorosoro.user.presentation;

import com.sorosoro.auth.domain.AuthUserPrincipal;
import com.sorosoro.auth.dto.MessageResponse;
import com.sorosoro.user.application.UserService;
import com.sorosoro.user.dto.UserMeResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public UserMeResponse getMe(@AuthenticationPrincipal AuthUserPrincipal principal) {
        return userService.getMe(principal.userId());
    }

    @DeleteMapping("/me")
    public ResponseEntity<MessageResponse> withdraw(@AuthenticationPrincipal AuthUserPrincipal principal) {
        userService.withdraw(principal.userId());
        return ResponseEntity.ok(new MessageResponse("회원 탈퇴가 완료되었습니다."));
    }
}
