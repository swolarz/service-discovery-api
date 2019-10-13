package com.put.swolarz.servicediscoveryapi.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNEXPECTED_ERROR("Unexpected exception occurred"),
    INVALID_PAGE_REQUESTED("Invalid page parameters for retrieving pageable list of objects"),
    ENTITY_ID_NOT_SPECIFIED("Id of the entity not specified"),
    HOST_NODE_NOT_FOUND("Not found host node with given id"),
    HOST_NODE_ALREADY_EXISTS("The host node with given id already exists");

    private String message;
}
