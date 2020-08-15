package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;


@Data
@Builder(toBuilder = true)
public class HostNodeData {

    @NotNull
    private String name;

    @NotNull
    private String status;

    @NotNull
    private String operatingSystem;

    private long dataCenterId;

    private String dataVersionToken;
}
