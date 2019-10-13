package com.put.swolarz.servicediscoveryapi.api.dto.hostnode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.put.swolarz.servicediscoveryapi.domain.model.discovery.DataCenter;
import com.put.swolarz.servicediscoveryapi.domain.model.discovery.HostNode;
import com.put.swolarz.servicediscoveryapi.domain.model.discovery.HostStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HostNodeDto {
    private Long id;
    private String name;
    private String status;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
    private LocalDateTime launchedAt;

    private Long dataCenterId;
    private String dataCenterName;

    private String operatingSystem;


    public HostNode toEntity() {
        return HostNode.builder()
                .id(id)
                .name(name)
                .status(HostStatus.valueOf(status.toUpperCase()))
                .launchedAt(launchedAt)
                .dataCenter(new DataCenter(dataCenterId))
                .os(operatingSystem)
                .build();
    }

    public static HostNodeDto fromEntity(HostNode hostNode) {
        return HostNodeDto.builder()
                .id(hostNode.getId())
                .name(hostNode.getName())
                .status(hostNode.getStatus().name())
                .launchedAt(hostNode.getLaunchedAt())
                .dataCenterId(hostNode.getDataCenter().getId())
                .dataCenterName(hostNode.getDataCenter().getName())
                .operatingSystem(hostNode.getOs())
                .build();
    }
}
