package com.put.swolarz.servicediscoveryapi.domain.common.exception;

import lombok.Getter;


public class BusinessException extends Exception {

    @Getter
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
        return code.getMessageFormat(msgArgs);
    }
}
