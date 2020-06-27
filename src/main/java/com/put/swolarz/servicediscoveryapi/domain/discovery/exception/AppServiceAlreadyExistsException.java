package com.put.swolarz.servicediscoveryapi.domain.discovery.exception;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.ErrorCode;

public class AppServiceAlreadyExistsException extends BusinessException {

    public AppServiceAlreadyExistsException(long appServiceId) {
        super(ErrorCode.APP_SERVICE_ALREADY_EXISTS, appServiceId);
    }

    public AppServiceAlreadyExistsException(long appServiceId, Throwable cause) {
        super(ErrorCode.APP_SERVICE_ALREADY_EXISTS, cause, appServiceId);
    }
}
