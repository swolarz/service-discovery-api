package com.put.swolarz.servicediscoveryapi.api.dto.appservice;

import com.put.swolarz.servicediscoveryapi.domain.model.discovery.AppService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class AppServiceDto {

    private long id;
    private String name;
    private String serviceVersion;

    public static AppServiceDto fromEntity(AppService appService) {
        return AppServiceDto.builder()
                .id(appService.getId())
                .name(appService.getName())
                .serviceVersion(appService.getServiceVersion())
                .build();
    }
}
