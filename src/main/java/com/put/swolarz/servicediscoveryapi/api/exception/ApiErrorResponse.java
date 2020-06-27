package com.put.swolarz.servicediscoveryapi.api.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
public class ApiErrorResponse {
    private String errMsg;
    private LocalDateTime time;
}
