package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.ReadWriteTransaction;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.MigrationRequest;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.MigrationResult;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@ReadWriteTransaction
@RequiredArgsConstructor
@Slf4j
class OrchestrationServiceImpl implements OrchestrationService {

    private final ServiceInstanceRepository serviceInstanceRepository;
    private final HostNodeRepository hostNodeRepository;


    @Override
    public MigrationResult migrateServiceInstances(MigrationRequest migrationRequest) throws HostNodeNotFoundException {
        long sourceHostId = migrationRequest.getSourceHostId();
        long targetHostId = migrationRequest.getTargetHostId();

        if (hostNodeRepository.existsById(sourceHostId))
            throw new HostNodeNotFoundException(sourceHostId);

        HostNode targetHost = hostNodeRepository.findById(targetHostId)
                .orElseThrow(() -> new HostNodeNotFoundException(targetHostId));

        if (sourceHostId == targetHostId) {
            return MigrationResult.builder().migratedInstances(0).build();
        }

//        Set<Integer> usedPorts = serviceInstanceRepository.findPortsByHostId(targetHostId)
//                .stream()
//                .map(ServiceInstancePort::getPort)
//                .collect(Collectors.toSet());

        List<Integer> usedPorts = targetHost.getUsedPorts();

        AtomicInteger migrationCount = new AtomicInteger();
        HostPortResolver portResolver = new HostPortResolver(usedPorts);

        serviceInstanceRepository.findAllByHostId(sourceHostId)
                .forEach(serviceInstance -> {
                    if (portResolver.isAssigned(serviceInstance.getPort())) {
                        int newPort = portResolver.resolveUnusedPort(targetHost);

                        serviceInstance.setPort(newPort);
                        portResolver.markAssigned(newPort);
                    }

                    serviceInstance.setHost(targetHost);
                    migrationCount.getAndIncrement();
                });

        return MigrationResult.builder().migratedInstances(migrationCount.get()).build();
    }
}
