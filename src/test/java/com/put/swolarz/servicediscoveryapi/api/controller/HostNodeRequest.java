package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
@AllArgsConstructor
class HostNodeRequest {

    private String name;
    private String status;
    private String operatingSystem;
    private long dataCenterId;

    private String dataVersionToken;


    public HostNodeRequest(String name, String status, String operatingSystem, long dataCenterId) {
        this.name = name;
        this.status = status;
        this.operatingSystem = operatingSystem;
        this.dataCenterId = dataCenterId;
        this.dataVersionToken = null;
    }
}
