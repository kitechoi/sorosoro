package com.sorosoro.common.response;

import com.sorosoro.common.exception.ErrorCode;

public record ErrorResponse(
        String code,
        String message,
        Object details
) {

    public static ErrorResponse of(ErrorCode errorCode) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static ErrorResponse of(ErrorCode errorCode, Object details) {
        return new ErrorResponse(errorCode.getCode(), errorCode.getMessage(), details);
    }
}
