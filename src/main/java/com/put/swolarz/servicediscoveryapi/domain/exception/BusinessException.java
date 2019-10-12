package com.put.swolarz.servicediscoveryapi.domain.exception;

import lombok.Getter;


@Getter
public class BusinessException extends Exception {

    private ErrorCode code;

    public BusinessException(ErrorCode code) {
        super();
        this.code = code;
    }

    public BusinessException(ErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(ErrorCode code, Throwable cause) {
        super(code.getMessage(), cause);
        this.code = code;
    }

    protected BusinessException(ErrorCode code, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }
}
