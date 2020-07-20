package com.put.swolarz.servicediscoveryapi.domain.discovery.exception;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.ErrorCode;

public class DataCenterAlreadyExistsException extends BusinessException {

    public DataCenterAlreadyExistsException(long dataCenterId) {
        super(ErrorCode.DATA_CENTER_ALREADY_EXISTS, dataCenterId);
    }

    public DataCenterAlreadyExistsException(long dataCenterId, Throwable cause) {
        super(ErrorCode.DATA_CENTER_ALREADY_EXISTS, cause, dataCenterId);
    }
}
