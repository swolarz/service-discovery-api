package com.put.swolarz.servicediscoveryapi.domain.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public enum ErrorCode {
    UNEXPECTED_ERROR("Unexpected exception occurred"),
    INVALID_PAGE_REQUESTED("Invalid page parameters for retrieving pageable list of objects");

    private String message;
}
