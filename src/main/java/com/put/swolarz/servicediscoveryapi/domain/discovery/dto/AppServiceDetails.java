package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class AppServiceDetails {
    private long id;
    private String name;
    private String serviceVersion;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private AppServiceInstancesInfo instancesInfo;
}
