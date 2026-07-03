package com.sorosoro.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sorosoro.auth.repository.RefreshTokenRepository;
import com.sorosoro.user.domain.User;
import com.sorosoro.user.domain.UserStatus;
import com.sorosoro.user.dto.UserMeResponse;
import com.sorosoro.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;

class UserServiceTest {

    private UserRepository userRepository;
    private RefreshTokenRepository refreshTokenRepository;
    private UserService userService;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
        userService = new UserService(userRepository, refreshTokenRepository);
    }

    @Test
    void getMeReturnsCurrentUser() {
        User user = User.builder()
                .kakaoId("12345")
                .nickname("soro")
                .profileImageUrl("https://profile")
                .build();
        ReflectionTestUtils.setField(user, "id", 1L);
        ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.of(2026, 7, 2, 10, 30));

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserMeResponse response = userService.getMe(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.nickname()).isEqualTo("soro");
        assertThat(response.profileImageUrl()).isEqualTo("https://profile");
        assertThat(response.status()).isEqualTo(UserStatus.ACTIVE);
        assertThat(response.createdAt()).isEqualTo(LocalDateTime.of(2026, 7, 2, 10, 30));
    }

    @Test
    void withdrawChangesUserStatusAndDeletesRefreshTokens() {
        User user = User.builder()
                .kakaoId("12345")
                .nickname("soro")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        userService.withdraw(1L);

        assertThat(user.getStatus()).isEqualTo(UserStatus.DELETED);
        verify(refreshTokenRepository).deleteByUser(user);
    }
}
