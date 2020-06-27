package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import lombok.Data;


@Data
public class ServiceInstanceData {
    private long appServiceId;
    private long hostNodeId;
    private int port;
}
