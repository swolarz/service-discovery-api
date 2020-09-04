package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.*;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.AppServicesService;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.AppServiceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/services")
@RequiredArgsConstructor
class AppServiceController {
    private static final String POE_TOKEN_HEADER = PostOnceExactlyHandler.POE_HEADER;

    private final AppServicesService appServicesService;
    private final PostOnceExactlyHandler postOnceExactlyHandler;


    @GetMapping
    public ResponseEntity<ResultsPage<AppServiceDetails>> getAppServices(
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "perPage", required = false, defaultValue = "10") int perPage) {

        ResultsPage<AppServiceDetails> resultPage = appServicesService.getAppServices(page, perPage);
        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/{appServiceId}")
    public ResponseEntity<AppServiceDetails> getAppService(@PathVariable("appServiceId") long appServiceId) throws BusinessException {
        AppServiceDetails appService = appServicesService.getAppService(appServiceId);
        return ResponseEntity.ok(appService);
    }

    @GetMapping("/{appServiceId}/instances")
    public ResponseEntity<ResultsPage<ServiceInstanceDetails>> getAppServiceInstances(
            @PathVariable("appServiceId") long appServiceId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "perPage", required = false, defaultValue = "10") int perPage) throws BusinessException {

        ResultsPage<ServiceInstanceDetails> resultsPage = appServicesService.getAppServiceInstances(appServiceId, page, perPage);
        return ResponseEntity.ok(resultsPage);
    }

    @GetMapping("/{appServiceId}/instances/{instanceId}")
    public ResponseEntity<ServiceInstanceDetails> getAppServiceInstanceDetails(@PathVariable("appServiceId") long appServiceId,
                                                                               @PathVariable("instanceId") long instanceId)
            throws BusinessException {

        ServiceInstanceDetails instanceDetails = appServicesService.getAppServiceInstance(appServiceId, instanceId);
        return ResponseEntity.ok(instanceDetails);
    }

    @PostMapping("/{appServiceId}/instances")
    public ResponseEntity<ServiceInstanceDetails> postAppServiceInstance(@PathVariable("appServiceId") long appServiceId,
                                                                         @RequestBody ServiceInstanceData serviceInstanceData,
                                                                         @RequestHeader(POE_TOKEN_HEADER) String poeToken)
            throws BusinessException, PostOnceExactlyException {

        postOnceExactlyHandler.ensurePostOnceExactly(poeToken);

        serviceInstanceData.setAppServiceId(appServiceId);
        ServiceInstanceDetails serviceInstance = appServicesService.addAppServiceInstance(serviceInstanceData);

        return ResponseEntity.ok(serviceInstance);
    }

    @DeleteMapping("/{appServiceId}/instances/{instanceId}")
    public ResponseEntity<Long> deleteAppServiceInstance(@PathVariable("appServiceId") long appServiceId,
                                                         @PathVariable("instanceId") long instanceId) throws BusinessException {

        appServicesService.removeAppServiceInstance(instanceId, appServiceId);
        return ResponseEntity.ok(instanceId);
    }

    @PostMapping
    public ResponseEntity<AppServiceDetails> postAppService(@RequestBody AppServiceData appService,
                                                            @RequestHeader(POE_TOKEN_HEADER) String poeToken)
            throws PostOnceExactlyException {

        postOnceExactlyHandler.ensurePostOnceExactly(poeToken);

        AppServiceDetails appServiceDetails = appServicesService.createAppService(appService);
        return ResponseEntity.ok(appServiceDetails);
    }

    @PostMapping("/{appServiceId}")
    public ResponseEntity<AppServiceDetails> postAppServiceWithId(@PathVariable("appServiceId") long appServiceId,
                                                                  @RequestBody AppServiceData appService) throws BusinessException {

        AppServiceDetails appServiceDetails = appServicesService.updateAppService(appServiceId, appService);
        return ResponseEntity.ok(appServiceDetails);
    }

    @PutMapping("/{appServiceId}")
    public ResponseEntity<AppServiceDetails> putAppService(@PathVariable("appServiceId") long appServiceId,
                                                           @RequestBody AppServiceData appService) throws BusinessException {

        AppServiceDetails appServiceDetails = appServicesService.createOrUpdateAppService(appServiceId, appService);
        return ResponseEntity.ok(appServiceDetails);
    }

    @PatchMapping("/{appServiceId}")
    public ResponseEntity<AppServiceDetails> patchAppService(@PathVariable("appServiceId") long appServiceId,
                                                             @RequestBody AppServiceUpdateData updateData) throws BusinessException {

        AppServiceDetails appServiceDetails = appServicesService.updateAppService(appServiceId, updateData);
        return ResponseEntity.ok(appServiceDetails);
    }

    @DeleteMapping("/{appServiceId}")
    public ResponseEntity<Long> deleteAppService(@PathVariable("appServiceId") long appServiceId) throws BusinessException {
        appServicesService.removeAppService(appServiceId);
        return ResponseEntity.ok(appServiceId);
    }

    @PostMapping("/{appServiceId}/scale")
    public ResponseEntity<ServiceScaleResult> scaleAppService(@PathVariable("appServiceId") long appServiceId,
                                                              @RequestParam(name = "replication") int replication)
            throws BusinessException {

        if (replication < 0)
            throw new IllegalArgumentException("Not appropriate replication factor value");

        if (replication > 100)
            throw new IllegalArgumentException("Replication factor must not exceed 100");

        ServiceScaleResult result = appServicesService.scaleService(appServiceId, replication);
        return ResponseEntity.ok(result);
    }
}
