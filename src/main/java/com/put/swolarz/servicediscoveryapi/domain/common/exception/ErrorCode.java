package com.put.swolarz.servicediscoveryapi.domain.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    DATA_CENTER_NOT_FOUND("Data center with id = %d not found"),
    HOST_NODE_NOT_FOUND("Host node with id = %d not found"),
    HOST_PORT_IN_USE("Given port (%d) is already in use on host with id = %d"),
    APP_SERVICE_NOT_FOUND("App service with given id = %d not found"),
    SERVICE_INSTANCE_NOT_FOUND("The service instance with id = %d not found for app service with id = %d");

    private final String messageFormat;

    public String getMessageFormat(Object... msgArgs) {
        return String.format(messageFormat, msgArgs);
    }
}
