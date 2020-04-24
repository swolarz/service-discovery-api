package com.put.swolarz.servicediscoveryapi.api.dto.appservice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
public class AppServicesPageDto {
    private int page;
    private int perPage;
    private long allFound;

    private List<AppServiceDto> appServices;
}
