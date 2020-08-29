package com.put.swolarz.servicediscoveryapi.api.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
class ServiceInstanceRequest {
    private long appServiceId;
    private long hostNodeId;
    private int port;
}
