package com.put.swolarz.servicediscoveryapi.domain.discovery;

import java.util.Arrays;

enum InstanceStatus {
    RUNNING, STARTING, STOPPED;

    public String getValue() {
        return name().toLowerCase();
    }

    public static InstanceStatus fromValue(String status) {
        return Arrays.stream(values())
                .filter(instanceStatus -> instanceStatus.getValue().equals(status))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("Invalid app instance status value (%s)", status))
                );
    }
}
