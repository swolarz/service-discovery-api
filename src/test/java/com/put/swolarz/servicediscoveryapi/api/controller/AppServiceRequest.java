package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class AppServiceRequest {

    private String name;
    private String serviceVersion;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataVersionToken;
}
