package com.put.swolarz.servicediscoveryapi.domain.discovery;

import java.util.List;

interface HostNodeRepositoryCustom {

    List<Integer> getHostUsedPorts(HostNode hostNode);
}
