package com.put.swolarz.servicediscoveryapi.domain.discovery.exception;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.ErrorCode;


public class AppServiceNotFoundException extends BusinessException {

    public AppServiceNotFoundException(long appServiceId) {
        super(ErrorCode.APP_SERVICE_NOT_FOUND, appServiceId);
    }

    public AppServiceNotFoundException(long appServiceId, Throwable cause) {
        super(ErrorCode.APP_SERVICE_NOT_FOUND, cause, appServiceId);
    }
}
