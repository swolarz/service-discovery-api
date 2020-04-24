package com.put.swolarz.servicediscoveryapi.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNEXPECTED_ERROR("Unexpected exception occurred"),
    INVALID_PAGE_REQUESTED("Invalid page parameters for retrieving pageable list of objects"),
    ENTITY_ID_NOT_SPECIFIED("Id of the entity not specified"),
    HOST_NODE_NOT_FOUND("Host node with given id not found"),
    HOST_NODE_ALREADY_EXISTS("The host node with given id already exists"),
    APP_SERVICE_NOT_FOUND("App service with given id=%d not found");

    private String message;

    public String getMessage(Object... msgArgs) {
        return String.format(message, msgArgs);
    }
}
