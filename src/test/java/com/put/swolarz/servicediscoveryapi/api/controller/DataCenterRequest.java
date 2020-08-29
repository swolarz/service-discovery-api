package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
class DataCenterRequest {

    private String name;
    private String location;

    private String dataVersionToken;


    public DataCenterRequest(String name, String location) {
        this.name = name;
        this.location = location;

        this.dataVersionToken = null;
    }
}
