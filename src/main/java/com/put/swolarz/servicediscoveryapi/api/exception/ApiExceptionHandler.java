package com.put.swolarz.servicediscoveryapi.api.exception;

import com.put.swolarz.servicediscoveryapi.api.controller.PostOnceExactlyException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.persistence.OptimisticLockException;
import java.time.LocalDateTime;


@RestControllerAdvice
@Slf4j
class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({
            AppServiceNotFoundException.class,
            ServiceInstanceNotFoundException.class,
            DataCenterNotFoundException.class,
            HostNodeNotFoundException.class
    })
    public ResponseEntity<ApiErrorResponse> handleNotFoundException(BusinessException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(makeErrorResponse(e.getMessage()));
    }

    @ExceptionHandler({
            IllegalArgumentException.class,
            HostPortAlreadyInUse.class
    })
    public ResponseEntity<ApiErrorResponse> handleBadRequestException(Exception e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(makeErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ResponseEntity<ApiErrorResponse> handleOptimisticLockException(OptimisticLockException e) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(makeErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(PostOnceExactlyException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateRequestException(PostOnceExactlyException e) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(makeErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorResponse> handleNotEnoughHostsException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(makeErrorResponse(e.getMessage()));
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
