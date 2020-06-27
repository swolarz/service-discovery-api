package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ServiceInstanceDetails {
    private long id;

    private long appServiceId;
    private String appServiceName;

    private long hostNodeId;
    private String hostNodeName;
    private int port;

    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime startedAt;
}
