package com.put.swolarz.servicediscoveryapi.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/orchestration")
@RequiredArgsConstructor
class OrchestratorController {

    @PostMapping("/migration")
    public ResponseEntity<Object> postMigration() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PostMapping("/replication")
    public ResponseEntity<Object> postReplication() {
        throw new UnsupportedOperationException("Not implemented");
    }

    @PostMapping("/shutdown")
    public ResponseEntity<Object> postShutdown() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
