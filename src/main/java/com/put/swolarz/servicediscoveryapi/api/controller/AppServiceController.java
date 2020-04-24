package com.put.swolarz.servicediscoveryapi.api.controller;

import com.put.swolarz.servicediscoveryapi.api.dto.appservice.AppServiceDto;
import com.put.swolarz.servicediscoveryapi.api.dto.appservice.AppServicesPageDto;
import com.put.swolarz.servicediscoveryapi.domain.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.exception.ErrorCode;
import com.put.swolarz.servicediscoveryapi.domain.model.common.EntitiesPage;
import com.put.swolarz.servicediscoveryapi.domain.model.discovery.AppService;
import com.put.swolarz.servicediscoveryapi.domain.service.AppServicesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AppServiceController {

    private final AppServicesService appServicesService;


    @GetMapping("/services")
    public ResponseEntity<AppServicesPageDto> getAppServices(@RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                             @RequestParam(value = "perPage", required = false, defaultValue = "10") int perPage)
            throws BusinessException {

        EntitiesPage<AppService> appServicesPage = appServicesService.getAppServices(page, perPage);

        List<AppServiceDto> appServiceDtos = appServicesPage.getEntities().stream()
                .map(AppServiceDto::fromEntity)
                .collect(Collectors.toList());

        AppServicesPageDto resultPage = AppServicesPageDto.builder()
                .allFound(appServicesPage.getTotalNumber())
                .page(page)
                .perPage(perPage)
                .appServices(appServiceDtos)
                .build();

        return ResponseEntity.ok(resultPage);
    }

    @GetMapping("/services/{appServiceId}")
    public ResponseEntity<AppServiceDto> getAppService(@PathVariable("appServiceId") long appServiceId) throws BusinessException {
        AppService appService = appServicesService.getAppService(appServiceId)
                .orElseThrow(() -> new BusinessException(ErrorCode.APP_SERVICE_NOT_FOUND));

        return ResponseEntity.ok(AppServiceDto.fromEntity(appService));
    }

    @GetMapping("/services/{appServiceId}/instances")
    public ResponseEntity<AppServicesPageDto> getAppServiceInstances(@PathVariable("appServiceId") long appServiceId)
        throws BusinessException {

        throw new UnsupportedOperationException("Method not implemented");
    }

    @PostMapping("/services")
    public ResponseEntity<AppServiceDto> postNewAppService(@RequestBody AppServiceDto appService) throws BusinessException {

        throw new UnsupportedOperationException("Method not implemented");
    }

    @PostMapping("/service/{appServiceId}")
    public ResponseEntity<AppServiceDto> postAppServiceWithId(@PathVariable("appServiceid") long appServiceId) throws BusinessException {

        throw new UnsupportedOperationException("Method not implemented");
    }

    @GetMapping("/services/{appServiceId}/instances/{instanceId}")
    public ResponseEntity<AppServiceDto> getAppServiceInstanceDetails(@PathVariable("appServiceid") long appServiceId,
                                                                      @PathVariable("instanceId") long instanceId)
        throws BusinessException {

        throw new UnsupportedOperationException("Method not implemented");
    }
}
