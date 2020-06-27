package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.common.PatchUpdateDictionary;
import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.util.DtoUtils;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.*;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.AppServiceAlreadyExistsException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.AppServiceNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.ServiceInstanceNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
class AppServicesServiceImpl implements AppServicesService {

    private final AppServiceRepository appServiceRepository;
    private final ServiceInstanceRepository serviceInstanceRepository;


    @Override
    public ResultsPage<AppServiceDetails> getAppServices(int page, int perPage) {
        PageRequest pageRequest = PageRequest.of(page, perPage);
        Page<AppService> resultsPage = appServiceRepository.findAll(pageRequest);

        return DtoUtils.toDtoResultsPage(
                resultsPage, pageRequest,
                appService -> toAppServiceDetails(
                        appService, getTopServiceInstances(appService)
                ),
                AppServiceDetails.class
        );
    }

    @Override
    public AppServiceDetails getAppService(long appServiceId) throws AppServiceNotFoundException {
        AppService appService = appServiceRepository.findById(appServiceId)
                .orElseThrow(() -> new AppServiceNotFoundException(appServiceId));

        return toAppServiceDetails(appService, getTopServiceInstances(appService));
    }

    private Page<ServiceInstance> getTopServiceInstances(@NonNull AppService appService) {
        return serviceInstanceRepository.findByServiceId(appService.getId(), PageRequest.of(0, 5));
    }

    @Override
    public ResultsPage<ServiceInstanceDetails> getAppServiceInstances(long appServiceId, int page, int perPage) throws AppServiceNotFoundException {
        appServiceRepository.findById(appServiceId)
                .orElseThrow(() -> new AppServiceNotFoundException(appServiceId));

        PageRequest pageRequest = PageRequest.of(page, perPage);
        Page<ServiceInstance> resultsPage = serviceInstanceRepository.findByServiceId(appServiceId, pageRequest);

        return DtoUtils.toDtoResultsPage(resultsPage, pageRequest, this::toServiceInstanceDetails, ServiceInstanceDetails.class);
    }

    @Override
    public ServiceInstanceDetails getAppServiceInstance(long appServiceId, long serviceInstanceId) throws AppServiceNotFoundException, ServiceInstanceNotFoundException {
        appServiceRepository.findById(appServiceId)
                .orElseThrow(() -> new AppServiceNotFoundException(appServiceId));

        ServiceInstance serviceInstance = serviceInstanceRepository.findByIdAndServiceId(serviceInstanceId, appServiceId)
                .orElseThrow(() -> new ServiceInstanceNotFoundException(appServiceId, serviceInstanceId));

        return toServiceInstanceDetails(serviceInstance);
    }

    @Override
    public AppServiceDetails createAppService(AppServiceData appService) throws AppServiceAlreadyExistsException {
        if (appService.getId() != null) {
            AppService service = appServiceRepository.findById(appService.getId())
                    .orElseThrow(() -> )
        }
    }

    @Override
    public AppServiceDetails updateAppService(AppServiceData appService, boolean create) throws AppServiceNotFoundException {
        return null;
    }

    @Override
    public AppServiceDetails updateAppService(long appServiceId, PatchUpdateDictionary updateAttributes) throws BusinessException {
        return null;
    }

    @Override
    public void removeAppService(long appServiceId) throws AppServiceNotFoundException {
        try {
            appServiceRepository.deleteAllInBatch();
        }
        catch (EmptyResultDataAccessException e) {
            throw new AppServiceNotFoundException(appServiceId, e);
        }
    }

    private AppServiceDetails toAppServiceDetails(AppService appService, Page<ServiceInstance> topInstances) {
        return AppServiceDetails.builder()
                .id(appService.getId())
                .name(appService.getName())
                .serviceVersion(appService.getServiceVersion())
                .instancesInfo(
                        AppServiceInstancesInfo.builder()
                                .totalCount(topInstances.getTotalElements())
                                .topInstances(
                                        topInstances.stream()
                                                .map(this::toServiceInstanceInfo)
                                                .collect(Collectors.toList())
                                )
                                .build()
                )
                .build();
    }

    private ServiceInstanceInfo toServiceInstanceInfo(ServiceInstance serviceInstance) {
        return ServiceInstanceInfo.builder()
                .instanceId(serviceInstance.getId())
                .hostId(serviceInstance.getHost().getId())
                .hostName(serviceInstance.getHost().getName())
                .status(serviceInstance.getStatus().getValue())
                .build();
    }

    private ServiceInstanceDetails toServiceInstanceDetails(ServiceInstance serviceInstance) {
        return ServiceInstanceDetails.builder()
                .id(serviceInstance.getId())
                .appServiceId(serviceInstance.getService().getId())
                .appServiceName(serviceInstance.getService().getName())
                .hostNodeId(serviceInstance.getHost().getId())
                .hostNodeName(serviceInstance.getHost().getName())
                .port(serviceInstance.getPort())
                .status(serviceInstance.getStatus().getValue())
                .startedAt(serviceInstance.getStartedAt())
                .build();
    }
}
