package com.sorosoro.common.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COM-001", "요청값이 올바르지 않습니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COM-002", "입력값 검증에 실패했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COM-999", "서버 내부 오류가 발생했습니다."),

    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, "AUTH-001", "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-002", "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH-003", "Refresh Token이 유효하지 않습니다."),
    KAKAO_LOGIN_FAILED(HttpStatus.BAD_REQUEST, "AUTH-004", "카카오 로그인에 실패했습니다."),
    REFRESH_TOKEN_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH-005", "Refresh Token이 필요합니다."),
    REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH-006", "로그인 세션이 만료되었습니다. 다시 로그인해주세요."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER-001", "사용자 정보를 찾을 수 없습니다."),
    DELETED_USER(HttpStatus.CONFLICT, "USER-002", "탈퇴한 사용자입니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
