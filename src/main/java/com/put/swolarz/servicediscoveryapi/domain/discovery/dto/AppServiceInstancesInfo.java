package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
public class AppServiceInstancesInfo {
    private long totalCount;
    private List<ServiceInstanceInfo> topInstances;
}
