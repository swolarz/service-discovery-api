package com.put.swolarz.servicediscoveryapi.domain.discovery.exception;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.ErrorCode;

public class HostNodeAlreadyExistsException extends BusinessException {

    public HostNodeAlreadyExistsException(long hostNodeId) {
        super(ErrorCode.HOST_NODE_ALREADY_EXISTS, hostNodeId);
    }

    public HostNodeAlreadyExistsException(long hostNodeId, Throwable cause) {
        super(ErrorCode.HOST_NODE_ALREADY_EXISTS, cause, hostNodeId);
    }
}
