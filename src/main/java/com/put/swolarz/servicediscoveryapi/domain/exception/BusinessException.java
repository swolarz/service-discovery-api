package com.put.swolarz.servicediscoveryapi.domain.exception;

import lombok.Getter;


@Getter
public class BusinessException extends Exception {
    private ErrorCode code;
    private Object[] msgArgs;

    public BusinessException(ErrorCode code, Object... msgArgs) {
        super();
        this.code = code;
        this.msgArgs = msgArgs;
    }

    public BusinessException(ErrorCode code, Throwable cause, Object... msgArgs) {
        super(cause);
        this.code = code;
        this.msgArgs = msgArgs;
    }

    @Override
    public String getMessage() {
        return code.getMessage(msgArgs);
    }
}
