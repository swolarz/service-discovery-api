package com.put.swolarz.servicediscoveryapi.domain.discovery.exception;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.ErrorCode;


public class HostPortAlreadyInUse extends BusinessException {

    public HostPortAlreadyInUse(long hostId, int port) {
        super(ErrorCode.HOST_PORT_IN_USE, port, hostId);
    }

    public HostPortAlreadyInUse(long hostId, int port, Throwable cause) {
        super(ErrorCode.HOST_PORT_IN_USE, cause, port, hostId);
    }
}
