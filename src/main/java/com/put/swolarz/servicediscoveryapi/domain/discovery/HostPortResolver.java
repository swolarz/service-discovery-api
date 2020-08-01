package com.put.swolarz.servicediscoveryapi.domain.discovery;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


class HostPortResolver {
    private static final int UNUSED_PORT_LOWER_BOUND = 49152;
    private static final int UNUSED_PORT_UPPER_BOUND = 65535;

    private final Set<Integer> usedPorts;
    private int lastCheckedPort;


    public HostPortResolver(Collection<Integer> usedPorts) {
        this.usedPorts = new HashSet<>(usedPorts);
        this.lastCheckedPort = UNUSED_PORT_LOWER_BOUND - 1;
    }

    public int resolveUnusedPort(HostNode targetHost) {
        while (lastCheckedPort < UNUSED_PORT_UPPER_BOUND) {
            lastCheckedPort++;

            if (!usedPorts.contains(lastCheckedPort))
                return lastCheckedPort;
        }

        throw new IllegalStateException(
                String.format(
                        "Failed to assign a valid port for service instance at host (id = %d)",
                        targetHost.getId()
                )
        );
    }

    public boolean isAssigned(int port) {
        return usedPorts.contains(port);
    }

    public void markAssigned(int port) {
        usedPorts.add(port);
    }
}
