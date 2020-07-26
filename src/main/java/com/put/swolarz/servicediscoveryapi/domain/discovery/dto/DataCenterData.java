package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
public class DataCenterData {

    @NotNull
    private String name;

    @NotNull
    private String location;
}
