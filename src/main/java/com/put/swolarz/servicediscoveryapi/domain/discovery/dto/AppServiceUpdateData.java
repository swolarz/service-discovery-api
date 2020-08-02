package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;

import java.util.Optional;


@Getter
public class AppServiceUpdateData {
    private Optional<String> name;
    private Optional<String> serviceVersion;
    private String dataVersionToken;

    public AppServiceUpdateData() {
        this.name = Optional.empty();
        this.serviceVersion = Optional.empty();
    }

    @JsonSetter(value = "name", nulls = Nulls.FAIL)
    public void setName(String name) {
        this.name = Optional.of(name);
    }

    @JsonSetter(value = "serviceVersion", nulls = Nulls.FAIL)
    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = Optional.of(serviceVersion);
    }
}
