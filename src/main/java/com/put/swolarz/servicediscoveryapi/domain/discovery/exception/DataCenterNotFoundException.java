package com.put.swolarz.servicediscoveryapi.domain.discovery.exception;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.ErrorCode;

public class DataCenterNotFoundException extends BusinessException {

    public DataCenterNotFoundException(long dataCenterId) {
        super(ErrorCode.DATA_CENTER_NOT_FOUND, dataCenterId);
    }

    public DataCenterNotFoundException(long dataCenterId, Throwable cause) {
        super(ErrorCode.HOST_NODE_NOT_FOUND, cause, dataCenterId);
    }
}
