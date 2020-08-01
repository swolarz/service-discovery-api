package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.OrchestrationService;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.MigrationRequest;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.MigrationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/orchestration")
@RequiredArgsConstructor
class OrchestrationController {

    private final OrchestrationService orchestrationService;


    @PostMapping("/migration")
    public ResponseEntity<MigrationResult> postMigration(@RequestBody MigrationRequest migrationRequest) throws BusinessException {
        MigrationResult result = orchestrationService.migrateServiceInstances(migrationRequest);
        return ResponseEntity.ok(result);
    }
}
