package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class ServiceInstanceData {

    private long appServiceId;
    private long hostNodeId;
    private int port;
}
