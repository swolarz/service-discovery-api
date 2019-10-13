package com.put.swolarz.servicediscoveryapi.api.exception;

import com.put.swolarz.servicediscoveryapi.domain.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;


@RestControllerAdvice
@Slf4j
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private Map<ErrorCode, HttpStatus> errorStatus;

    public ApiExceptionHandler() {
        super();
        initErrorStatusMapping();
    }

    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ApiError> handleBusinessException(BusinessException e) {
        log.warn("Caught a handled business exception", e);

        ErrorCode errorCode = e.getCode();

        if (!errorStatus.containsKey(errorCode))
            errorCode = ErrorCode.UNEXPECTED_ERROR;

        HttpStatus responseStatus = errorStatus.get(errorCode);
        ApiError error = new ApiError(errorCode.getMessage());

        return ResponseEntity.status(responseStatus).body(error);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiError> handleUnhandledExceptionAndExpectTheUnexpected(Exception e) {
        log.error("From now, expect the unexcpected", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiError("Unknown error"));
    }

    private void initErrorStatusMapping() {
        errorStatus = new HashMap<>();

        errorStatus.put(ErrorCode.UNEXPECTED_ERROR, HttpStatus.INTERNAL_SERVER_ERROR);
        errorStatus.put(ErrorCode.INVALID_PAGE_REQUESTED, HttpStatus.BAD_REQUEST);
    }
}
