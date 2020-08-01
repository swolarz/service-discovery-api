package com.put.swolarz.servicediscoveryapi.api.exception;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;


@RestControllerAdvice
@Slf4j
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("Caught a handled business exception", e);

        switch (e.getCode()) {
            default:
                log.error("Caught unexpected business exception: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(makeErrorResponse("Server encountered unexpected behaviour"));
        }
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiErrorResponse> handleUnhandledExceptionAndExpectTheUnexpected(Exception e) {
        log.error("Expect the unexpected", e);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(makeErrorResponse("Server encountered unknown error"));
    }

    private static ApiErrorResponse makeErrorResponse(String message) {
        return new ApiErrorResponse(message, LocalDateTime.now());
    }
}
