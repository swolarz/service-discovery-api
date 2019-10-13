package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.api.dto.common.PatchUpdateDictionary;
import com.put.swolarz.servicediscoveryapi.api.dto.hostnode.HostNodeDto;
import com.put.swolarz.servicediscoveryapi.api.dto.hostnode.HostNodesPageDto;
import com.put.swolarz.servicediscoveryapi.domain.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.model.common.EntitiesPage;
import com.put.swolarz.servicediscoveryapi.domain.model.discovery.HostNode;
import com.put.swolarz.servicediscoveryapi.domain.service.HostNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HostNodeController {

    private final HostNodeService hostNodeService;


    @GetMapping("/hosts")
    public ResponseEntity<HostNodesPageDto> getHostNodes(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "perPage", required = false, defaultValue = "10") int perPage)
            throws BusinessException {

        EntitiesPage<HostNode> resultPage = hostNodeService.getHostNodesPage(page, perPage);
        List<HostNodeDto> hostNodeDtos = resultPage.getEntities().stream().map(HostNodeDto::fromEntity).collect(Collectors.toList());
        HostNodesPageDto responseBody = new HostNodesPageDto(page, perPage, resultPage.getTotalNumber(), hostNodeDtos);

        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/hosts/{hostId}")
    public ResponseEntity<HostNodeDto> getHostNode(@PathVariable("id") long hostId) throws BusinessException {
        // Assuming that the response is non null, exception is thrown if not found
        HostNode hostNode = hostNodeService.getHostNode(hostId);
        return makeHostNodeResponse(hostNode);
    }

    @PostMapping("/hosts")
    public ResponseEntity<HostNodeDto> postHostNode(@RequestBody HostNodeDto hostNode) throws BusinessException {
        HostNode newHostNode = hostNodeService.createHostNode(hostNode.toEntity());
        return makeHostNodeResponse(newHostNode);
    }

    @PostMapping("/hosts/{hostId}")
    public ResponseEntity<HostNodeDto> postHostNodeWithId(@PathVariable("hostId") long hostId,
                                                          @RequestBody HostNodeDto hostNode) throws BusinessException {
        hostNode.setId(hostId);
        HostNode newHostNode = hostNodeService.createHostNode(hostNode.toEntity());

        return makeHostNodeResponse(newHostNode);
    }

    @PutMapping("/hosts/{hostId}")
    public ResponseEntity<HostNodeDto> putHostNode(@PathVariable("hostId") long hostId,
                                                   @RequestBody HostNodeDto hostNode) throws BusinessException {
        hostNode.setId(hostId);
        HostNode updatedHostNode = hostNodeService.updateHostNode(hostNode.toEntity());

        return makeHostNodeResponse(updatedHostNode);
    }

    @PatchMapping("/hosts/{hostId}")
    public ResponseEntity<HostNodeDto> patchHostNode(@PathVariable("hostId") long hostId,
                                                     @RequestBody PatchUpdateDictionary updateDictionary) throws BusinessException{
        throw new UnsupportedOperationException("not implemented");
    }

    @DeleteMapping("/hosts/{hostId}")
    public ResponseEntity<Long> deleteHostNode(@PathVariable("hostId") long hostId) throws BusinessException {
        long deletedHostId = hostNodeService.removeHostNode(hostId);
        return ResponseEntity.ok(deletedHostId);
    }

    private static ResponseEntity<HostNodeDto> makeHostNodeResponse(HostNode hostNode) {
        HostNodeDto hostNodeResponse = HostNodeDto.fromEntity(hostNode);
        return ResponseEntity.ok(hostNodeResponse);
    }
}
