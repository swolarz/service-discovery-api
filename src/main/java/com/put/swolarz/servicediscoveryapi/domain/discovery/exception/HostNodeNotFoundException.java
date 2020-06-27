package com.put.swolarz.servicediscoveryapi.domain.discovery.exception;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.ErrorCode;

public class HostNodeNotFoundException extends BusinessException {

    public HostNodeNotFoundException(long hostNodeId) {
        super(ErrorCode.HOST_NODE_NOT_FOUND, hostNodeId);
    }

    public HostNodeNotFoundException(long hostNodeId, Throwable cause) {
        super(ErrorCode.HOST_NODE_NOT_FOUND, cause, hostNodeId);
    }
}
