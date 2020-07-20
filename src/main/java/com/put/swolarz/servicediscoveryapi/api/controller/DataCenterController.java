package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.DataCenterService;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/datacenter")
@RequiredArgsConstructor
class DataCenterController {

    private final DataCenterService dataCenterService;

    @GetMapping
    public ResponseEntity<ResultsPage<DataCenterDetails>> getDataCenters(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "perPage", required = false, defaultValue = "10") int perPage) {

        ResultsPage<DataCenterDetails> results = dataCenterService.getDataCenters(page, perPage);
        return ResponseEntity.ok(results);
    }

    @GetMapping("/{dataCenterId}")
    public ResponseEntity<DataCenterDetails> getDataCenter(@PathVariable("dataCenterId") long dataCenterId) throws BusinessException {
        DataCenterDetails dataCenter = dataCenterService.getDataCenter(dataCenterId);
        return ResponseEntity.ok(dataCenter);
    }

    @PostMapping
    public ResponseEntity<DataCenterDetails> postDataCenter(@RequestBody DataCenterData dataCenter) throws BusinessException {
        DataCenterDetails createdDataCenter = dataCenterService.createDataCenter(dataCenter);
        return ResponseEntity.ok(createdDataCenter);
    }

    @PatchMapping("/{dataCenterId}")
    public ResponseEntity<DataCenterDetails> patchDataCenter(@RequestBody Map<String, String> updateDictionary) throws BusinessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @DeleteMapping("/{dataCenterId}")
    public ResponseEntity<Long> deleteDataCenter(@PathVariable("dataCenterId") long dataCenterId) throws BusinessException {
        dataCenterService.removeDataCenter(dataCenterId);
        return ResponseEntity.ok(dataCenterId);
    }
}
