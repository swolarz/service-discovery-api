package com.put.swolarz.servicediscoveryapi.domain.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNEXPECTED_ERROR("Unexpected exception occurred"),
    INVALID_PAGE_REQUESTED("Invalid page parameters for retrieving pageable list of objects"),
    ENTITY_ID_NOT_SPECIFIED("Id of the entity not specified"),
    DATA_CENTER_NOT_FOUND("Data center with id = %d not found"),
    HOST_NODE_NOT_FOUND("Host node with id = %d not found"),
    HOST_NODE_ALREADY_EXISTS("The host node with id = %d already exists"),
    APP_SERVICE_NOT_FOUND("App service with given id = %d not found"),
    APP_SERVICE_ALREADY_EXISTS("The app service with id = %d already exists"),
    SERVICE_INSTANCE_NOT_FOUND("The service instance with id = %d not found for app service with id = %d");

    private final String messageFormat;

    public String getMessageFormat(Object... msgArgs) {
        return String.format(messageFormat, msgArgs);
    }
}
