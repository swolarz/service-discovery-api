package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class AppServiceData {
    private Long id;
    private String name;
    private String serviceVersion;
}
