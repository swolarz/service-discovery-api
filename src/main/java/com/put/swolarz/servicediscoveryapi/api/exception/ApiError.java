package com.put.swolarz.servicediscoveryapi.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class ApiError {
    private String errMsg;
}
