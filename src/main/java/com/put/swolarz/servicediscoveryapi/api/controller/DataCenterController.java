package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.DataCenterService;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterUpdateData;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/datacenters")
@RequiredArgsConstructor
class DataCenterController {
    private static final String POE_TOKEN_HEADER = PostOnceExactlyHandler.POE_HEADER;

    private final DataCenterService dataCenterService;
    private final PostOnceExactlyHandler postOnceExactlyHandler;


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
    public ResponseEntity<DataCenterDetails> postDataCenter(@RequestBody DataCenterData dataCenterData,
                                                            @RequestHeader(POE_TOKEN_HEADER) String poeToken)
            throws PostOnceExactlyException {

        postOnceExactlyHandler.ensurePostOnceExactly(poeToken);

        DataCenterDetails createdDataCenter = dataCenterService.createDataCenter(dataCenterData);
        return ResponseEntity.ok(createdDataCenter);
    }

    @PostMapping("/{dataCenterId}")
    public ResponseEntity<DataCenterDetails> postDataCenterWithId(@PathVariable("dataCenterId") long dataCenterId,
                                                                  @RequestBody DataCenterData dataCenterData) throws BusinessException {

        DataCenterDetails updatedDataCenter = dataCenterService.updateDataCenter(dataCenterId, dataCenterData);
        return ResponseEntity.ok(updatedDataCenter);
    }

    @PutMapping("/{dataCenterId}")
    public ResponseEntity<DataCenterDetails> putDataCenter(@PathVariable("dataCenterId") long dataCenterId,
                                                           @RequestBody DataCenterData dataCenterData) {

        DataCenterDetails dataCenter = dataCenterService.createOrUpdateDataCenter(dataCenterId, dataCenterData);
        return ResponseEntity.ok(dataCenter);
    }

    @PatchMapping("/{dataCenterId}")
    public ResponseEntity<DataCenterDetails> patchDataCenter(@PathVariable("dataCenterId") long dataCenterId,
                                                             @RequestBody DataCenterUpdateData updateData) throws BusinessException {

        DataCenterDetails updatedDataCenter = dataCenterService.updateDataCenter(dataCenterId, updateData);
        return ResponseEntity.ok(updatedDataCenter);
    }

    @DeleteMapping("/{dataCenterId}")
    public ResponseEntity<Long> deleteDataCenter(@PathVariable("dataCenterId") long dataCenterId) throws BusinessException {
        dataCenterService.removeDataCenter(dataCenterId);
        return ResponseEntity.ok(dataCenterId);
    }
}
