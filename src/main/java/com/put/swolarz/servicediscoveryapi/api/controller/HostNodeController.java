package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.api.dto.common.PatchUpdateDictionary;
import com.put.swolarz.servicediscoveryapi.api.dto.hostnode.HostNodeDto;
import com.put.swolarz.servicediscoveryapi.api.dto.hostnode.HostNodesPageDto;
//import com.put.swolarz.servicediscoveryapi.domain.service.HostNodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HostNodeController {

    // private final HostNodeService hostNodeService;


    @GetMapping("/hosts")
    public ResponseEntity<HostNodesPageDto> getHostNodes(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                         @RequestParam(value = "perPage", required = false, defaultValue = "10") int perPage) {
        throw new UnsupportedOperationException("not implemented");
    }

    @GetMapping("/hosts/{hostId}")
    public ResponseEntity<HostNodeDto> getHostNode(@PathVariable("id") long hostId) {
        throw new UnsupportedOperationException("not implemented");
    }

    @PostMapping("/hosts")
    public ResponseEntity<HostNodeDto> postHostNode(@RequestBody HostNodeDto hostNode) {
        throw new UnsupportedOperationException("not implemented");
    }

    @PostMapping("/hosts/{hostId}")
    public ResponseEntity<HostNodeDto> postHostNodeWithId(@RequestBody HostNodeDto hostNode) {
        throw new UnsupportedOperationException("not implemented");
    }

    @PutMapping("/hosts/{hostId}")
    public ResponseEntity<HostNodeDto> putHostNode(@RequestBody HostNodeDto hostNode) {
        throw new UnsupportedOperationException("not implemented");
    }

    @PatchMapping("/hosts/{hostId}")
    public ResponseEntity<HostNodeDto> patchHostNode(@PathVariable("hostId") long hostId,
                                                         @RequestBody PatchUpdateDictionary updateDictionary) {
        throw new UnsupportedOperationException();
    }

    @DeleteMapping("/hosts/{hostId}")
    public ResponseEntity<Long> deleteHostNode(@PathVariable("hostId") long hostId) {
        throw new UnsupportedOperationException("not implemented");
    }
}
