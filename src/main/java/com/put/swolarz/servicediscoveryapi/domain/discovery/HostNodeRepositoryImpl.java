package com.put.swolarz.servicediscoveryapi.domain.discovery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
class HostNodeRepositoryImpl implements HostNodeRepositoryCustom {

    private final ServiceInstanceRepository serviceInstanceRepository;

    @Override
    public List<Integer> getHostUsedPorts(HostNode hostNode) {
        return serviceInstanceRepository.findUsedPortsByHostId(hostNode.getId()).stream()
                .map(ServiceInstancePort::getPort)
                .collect(Collectors.toList());
    }
}
