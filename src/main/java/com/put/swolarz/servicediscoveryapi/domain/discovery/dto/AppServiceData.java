package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@Builder
@AllArgsConstructor
public class AppServiceData {

    @NotNull
    private String name;

    @NotNull
    private String serviceVersion;

    @NotNull
    private String dataVersionToken;
}
