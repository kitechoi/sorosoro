package com.sorosoro.user.application;

import com.sorosoro.auth.repository.RefreshTokenRepository;
import com.sorosoro.common.exception.ApiException;
import com.sorosoro.common.exception.ErrorCode;
import com.sorosoro.user.domain.User;
import com.sorosoro.user.dto.UserMeResponse;
import com.sorosoro.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public UserService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional(readOnly = true)
    public UserMeResponse getMe(Long userId) {
        User user = getUser(userId);
        if (user.isDeleted()) {
            throw new ApiException(ErrorCode.DELETED_USER);
        }
        return UserMeResponse.from(user);
    }

    @Transactional
    public void withdraw(Long userId) {
        User user = getUser(userId);
        user.withdraw();
        refreshTokenRepository.deleteByUser(user);
    }

    private User getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ErrorCode.USER_NOT_FOUND));
    }
}
