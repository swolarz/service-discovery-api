package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HostNodeDetails {
    private long id;

    private String name;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime launchedAt;

    private long dataCenterId;
    private String dataCenterName;

    private String operatingSystem;

    private int launchedInstances;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String dataVersionToken;
}
