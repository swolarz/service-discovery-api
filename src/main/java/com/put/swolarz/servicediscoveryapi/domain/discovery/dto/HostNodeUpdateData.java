package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;

import java.util.Optional;


@Getter
public class HostNodeUpdateData {
    private Optional<String> name;
    private Optional<String> status;

    private Optional<Long> dataCenterId;

    private Optional<String> operatingSystem;

    private String dataVersionToken;

    public HostNodeUpdateData() {
        this.name = Optional.empty();
        this.status = Optional.empty();
        this.dataCenterId = Optional.empty();
        this.operatingSystem = Optional.empty();
    }

    @JsonSetter(value = "name", nulls = Nulls.FAIL)
    public void setName(String name) {
        this.name = Optional.of(name);
    }

    @JsonSetter(value = "status", nulls = Nulls.FAIL)
    public void setStatus(String status) {
        this.status = Optional.of(status);
    }

    @JsonSetter(value = "dataCenterId", nulls = Nulls.FAIL)
    public void setDataCenterId(Long dataCenterId) {
        this.dataCenterId = Optional.of(dataCenterId);
    }

    @JsonSetter(value = "operatingSystem", nulls = Nulls.FAIL)
    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = Optional.of(operatingSystem);
    }
}
