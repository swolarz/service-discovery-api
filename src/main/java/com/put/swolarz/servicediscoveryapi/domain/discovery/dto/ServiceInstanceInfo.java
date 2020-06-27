package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ServiceInstanceInfo {
    private long instanceId;
    private long hostId;
    private String hostName;
    private String status;
}
