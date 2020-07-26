package com.put.swolarz.servicediscoveryapi.domain.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


public class UpdateDictErrors {

    @Data
    @Builder
    @AllArgsConstructor
    private static class DictError {
        private String attribute;
        private String value;
        private String reason;
    }


    private List<DictError> errors;


    public UpdateDictErrors() {
        this.errors = new ArrayList<>();
    }

    public void addError(String attribute, String value, String reason) {
        errors.add(
                DictError.builder()
                        .attribute(attribute)
                        .value(value)
                        .reason(reason)
                        .build()
        );
    }

    public boolean isEmpty() {
        return this.errors.isEmpty();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Some of the update properties are invalid:\n");

        for (DictError error : errors) {
            builder.append("\t");
            builder.append(error.getAttribute());
            builder.append(" = ");
            builder.append(error.getValue());
            builder.append("\t | reason = ");
            builder.append(error.getReason());
            builder.append("\n");
        }

        return builder.toString();
    }
}
