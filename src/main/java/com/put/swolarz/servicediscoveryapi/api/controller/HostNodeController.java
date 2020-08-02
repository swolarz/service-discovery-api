package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeData;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.HostNodeService;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeUpdateData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/hosts")
@RequiredArgsConstructor
class HostNodeController {
    private static final String POE_TOKEN_HEADER = PostOnceExactlyHandler.POE_HEADER;

    private final HostNodeService hostNodeService;
    private final PostOnceExactlyHandler postOnceExactlyHandler;


    @GetMapping
    public ResponseEntity<ResultsPage<HostNodeDetails>> getHostNodes(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "perPage", required = false, defaultValue = "10") int perPage) throws BusinessException {

        ResultsPage<HostNodeDetails> resultsPage = hostNodeService.getHostNodesPage(page, perPage);
        return ResponseEntity.ok(resultsPage);
    }

    @GetMapping("/{hostId}")
    public ResponseEntity<HostNodeDetails> getHostNode(@PathVariable("id") long hostId) throws BusinessException {

        HostNodeDetails hostNode = hostNodeService.getHostNode(hostId);
        return ResponseEntity.ok(hostNode);
    }

    @PostMapping
    public ResponseEntity<HostNodeDetails> postHostNode(@RequestBody HostNodeData hostNode,
                                                        @RequestHeader(POE_TOKEN_HEADER) String poeToken) throws BusinessException {

        postOnceExactlyHandler.ensurePostOnceExactly(poeToken);

        HostNodeDetails newHostNode = hostNodeService.createHostNode(hostNode);
        return ResponseEntity.ok(newHostNode);
    }

    @PostMapping("/{hostId}")
    public ResponseEntity<HostNodeDetails> postHostNodeWithId(@PathVariable("hostId") long hostId,
                                                              @RequestBody HostNodeData hostNode) throws BusinessException {

        HostNodeDetails newHostNode = hostNodeService.updateHostNode(hostId, hostNode);
        return ResponseEntity.ok(newHostNode);
    }

    @PutMapping("/{hostId}")
    public ResponseEntity<HostNodeDetails> putHostNode(@PathVariable("hostId") long hostId,
                                                       @RequestBody HostNodeData hostNode) throws BusinessException {

        HostNodeDetails updatedHostNode = hostNodeService.createOrUpdateHostNode(hostId, hostNode);
        return ResponseEntity.ok(updatedHostNode);
    }

    @PatchMapping("/{hostId}")
    public ResponseEntity<HostNodeDetails> patchHostNode(@PathVariable("hostId") long hostId,
                                                         @RequestBody HostNodeUpdateData updateData) throws BusinessException {

        HostNodeDetails updatedHostNode = hostNodeService.updateHostNode(hostId, updateData);
        return ResponseEntity.ok(updatedHostNode);
    }

    @DeleteMapping("/{hostId}")
    public ResponseEntity<Long> deleteHostNode(@PathVariable("hostId") long hostId) throws BusinessException {
        hostNodeService.removeHostNode(hostId);
        return ResponseEntity.ok().build();
    }
}
