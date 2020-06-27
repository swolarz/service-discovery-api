package com.put.swolarz.servicediscoveryapi.domain.discovery.exception;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.ErrorCode;

public class ServiceInstanceNotFoundException extends BusinessException {

    public ServiceInstanceNotFoundException(long appServiceId, long serviceInstanceId) {
        super(ErrorCode.SERVICE_INSTANCE_NOT_FOUND, serviceInstanceId, appServiceId);
    }

    public ServiceInstanceNotFoundException(long appServiceId, long serviceInstanceId, Throwable cause) {
        super(ErrorCode.SERVICE_INSTANCE_NOT_FOUND, cause, serviceInstanceId, appServiceId);
    }
}
