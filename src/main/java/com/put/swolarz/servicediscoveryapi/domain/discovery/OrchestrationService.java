package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.MigrationRequest;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.MigrationResult;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeNotFoundException;


public interface OrchestrationService {
    MigrationResult migrateServiceInstances(MigrationRequest migrationRequest) throws HostNodeNotFoundException;
}
