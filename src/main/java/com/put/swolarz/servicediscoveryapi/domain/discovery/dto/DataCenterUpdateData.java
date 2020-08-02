package com.put.swolarz.servicediscoveryapi.domain.discovery.dto;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import lombok.Getter;

import java.util.Optional;


@Getter
public class DataCenterUpdateData {
    private Optional<String> name;
    private String dataVersionToken;

    public DataCenterUpdateData() {
        this.name = Optional.empty();
    }

    @JsonSetter(value = "name", nulls = Nulls.FAIL)
    public void setName(String name) {
        this.name = Optional.of(name);
    }
}
