package com.put.swolarz.servicediscoveryapi.domain.discovery;

import java.util.Arrays;

enum HostStatus {
    UP, DOWN, RESTARTING;

    public String getValue() {
        return name().toLowerCase();
    }

    public static HostStatus fromValue(String status) {
        return Arrays.stream(values())
                .filter(hostStatus -> hostStatus.getValue().equals(status))
                .findFirst()
                .orElseThrow(
                        () -> new IllegalArgumentException(String.format("Invalid host status value (%s)", status))
                );
    }
}
