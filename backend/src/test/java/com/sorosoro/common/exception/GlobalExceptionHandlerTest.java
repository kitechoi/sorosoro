package com.sorosoro.common.exception;

import static org.assertj.core.api.Assertions.assertThat;

import com.sorosoro.common.response.ErrorResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handlesApiException() {
        ApiException exception = new ApiException(ErrorCode.INVALID_TOKEN);

        ResponseEntity<ErrorResponse> response = handler.handleApiException(exception);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody()).isEqualTo(ErrorResponse.of(ErrorCode.INVALID_TOKEN));
    }

    @Test
    void hidesInternalExceptionDetails() {
        ResponseEntity<ErrorResponse> response = handler.handleException(new IllegalStateException("secret"));

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody()).isEqualTo(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    @RestController
    static class ValidationSampleController {

        @PostMapping("/sample")
        void sample(@Valid @RequestBody SampleRequest request) {
        }
    }

    record SampleRequest(@NotBlank String name) {
    }
}
