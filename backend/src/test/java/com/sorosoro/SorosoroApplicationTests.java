package com.sorosoro;

import com.sorosoro.auth.infrastructure.KakaoOAuthClient;
import com.sorosoro.auth.repository.RefreshTokenRepository;
import com.sorosoro.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class SorosoroApplicationTests {

    @MockBean
    private KakaoOAuthClient kakaoOAuthClient;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void contextLoads() {
    }
}
