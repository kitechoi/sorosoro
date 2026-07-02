package com.sorosoro.common.response;

public record FieldErrorDetail(
        String field,
        String message,
        Object rejectedValue
) {
}
