package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder(toBuilder = true)
public class HostNodeData {
    private Long id;
    private String name;
    private String status;

    private long dataCenterId;

    private String operatingSystem;
}
