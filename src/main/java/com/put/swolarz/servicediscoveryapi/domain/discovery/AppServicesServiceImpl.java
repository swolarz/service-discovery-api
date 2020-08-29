package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.ReadOnlyTransaction;
import com.put.swolarz.servicediscoveryapi.domain.common.data.ReadWriteTransaction;
import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.common.util.DtoUtils;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.*;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.AppServiceNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostPortAlreadyInUse;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.ServiceInstanceNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.websync.OptimisticVersionHolder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@ReadWriteTransaction
@RequiredArgsConstructor
@Slf4j
class AppServicesServiceImpl implements AppServicesService {

    private final AppServiceRepository appServiceRepository;
    private final ServiceInstanceRepository serviceInstanceRepository;
    private final HostNodeRepository hostNodeRepository;

    private final OptimisticVersionHolder versionHolder;


    @Override
    @ReadOnlyTransaction
    public ResultsPage<AppServiceDetails> getAppServices(int page, int perPage) {
        PageRequest pageRequest = PageRequest.of(page, perPage);
        Page<AppService> resultsPage = appServiceRepository.findAll(pageRequest);

        return DtoUtils.toDtoResultsPage(
                resultsPage, pageRequest,
                appService -> toAppServiceDetails(
                        appService, getTopServiceInstances(appService), false
                ),
                AppServiceDetails.class
        );
    }

    @Override
    @ReadOnlyTransaction
    public AppServiceDetails getAppService(long appServiceId) throws AppServiceNotFoundException {
        AppService appService = appServiceRepository.findById(appServiceId)
                .orElseThrow(() -> new AppServiceNotFoundException(appServiceId));

        return toAppServiceDetails(appService, getTopServiceInstances(appService), true);
    }

    private Page<ServiceInstance> getTopServiceInstances(@NonNull AppService appService) {
        return serviceInstanceRepository.findByServiceId(appService.getId(), PageRequest.of(0, 5));
    }

    @Override
    @ReadOnlyTransaction
    public ResultsPage<ServiceInstanceDetails> getAppServiceInstances(long appServiceId, int page, int perPage)
            throws AppServiceNotFoundException {

        appServiceRepository.findById(appServiceId)
                .orElseThrow(() -> new AppServiceNotFoundException(appServiceId));

        PageRequest pageRequest = PageRequest.of(page, perPage);
        Page<ServiceInstance> resultsPage = serviceInstanceRepository.findByServiceId(appServiceId, pageRequest);

        return DtoUtils.toDtoResultsPage(resultsPage, pageRequest, this::toServiceInstanceDetails, ServiceInstanceDetails.class);
    }

    @Override
    @ReadOnlyTransaction
    public ServiceInstanceDetails getAppServiceInstance(long appServiceId, long serviceInstanceId)
            throws AppServiceNotFoundException, ServiceInstanceNotFoundException {

        appServiceRepository.findById(appServiceId)
                .orElseThrow(() -> new AppServiceNotFoundException(appServiceId));

        ServiceInstance serviceInstance = serviceInstanceRepository.findByIdAndServiceId(serviceInstanceId, appServiceId)
                .orElseThrow(() -> new ServiceInstanceNotFoundException(appServiceId, serviceInstanceId));

        return toServiceInstanceDetails(serviceInstance);
    }

    @Override
    public ServiceInstanceDetails addAppServiceInstance(ServiceInstanceData serviceInstanceData)
            throws AppServiceNotFoundException, HostNodeNotFoundException, HostPortAlreadyInUse {

        try {
            AppService service = appServiceRepository.findById(serviceInstanceData.getAppServiceId())
                    .orElseThrow(() -> new AppServiceNotFoundException(serviceInstanceData.getAppServiceId()));

            HostNode host = hostNodeRepository.findById(serviceInstanceData.getHostNodeId())
                    .orElseThrow(() -> new HostNodeNotFoundException(serviceInstanceData.getHostNodeId()));

            ServiceInstance instance = serviceInstanceRepository.saveAndFlush(
                    new ServiceInstance(service, host, serviceInstanceData.getPort())
            );

            return toServiceInstanceDetails(instance);
        }
        catch (DataIntegrityViolationException e) {
            long hostId = serviceInstanceData.getHostNodeId();
            int port = serviceInstanceData.getPort();

            if (serviceInstanceRepository.existsByHostIdAndPort(hostId, port))
                throw new HostPortAlreadyInUse(hostId, port, e);

            throw new RuntimeException("Unexpected service instance creation error", e);
        }
    }

    @Override
    public void removeAppServiceInstance(long serviceInstanceId, long appServiceId)
            throws AppServiceNotFoundException, ServiceInstanceNotFoundException {

        if (!appServiceRepository.existsById(appServiceId))
            throw new AppServiceNotFoundException(appServiceId);

        try {
            serviceInstanceRepository.deleteById(serviceInstanceId);
        }
        catch (EmptyResultDataAccessException e) {
            throw new ServiceInstanceNotFoundException(appServiceId, serviceInstanceId, e);
        }
    }

    @Override
    public AppServiceDetails createAppService(AppServiceData appServiceData) {
        AppService appService = appServiceRepository.save(
                new AppService(appServiceData.getName(), appServiceData.getServiceVersion())
        );

        return toAppServiceSaveInfo(appService);
    }

    @Override
    public AppServiceDetails createOrUpdateAppService(long appServiceId, AppServiceData appServiceData) {
        AppService appService = appServiceRepository.findById(appServiceId)
                .map(service -> {
                    EntityVersionUtils.validateEntityVersion(service, appServiceData.getDataVersionToken(), versionHolder);

                    service.setName(appServiceData.getName());
                    service.setServiceVersion(appServiceData.getServiceVersion());

                    return service;
                })
                .orElseGet(() -> appServiceRepository.save(
                        new AppService(appServiceData.getName(), appServiceData.getServiceVersion())
                ));

        return toAppServiceSaveInfo(appService);
    }


    @Override
    public AppServiceDetails updateAppService(long appServiceId, AppServiceData appServiceData) throws AppServiceNotFoundException {
        AppService appService = appServiceRepository.findById(appServiceId)
                .orElseThrow(() -> new AppServiceNotFoundException(appServiceId));

        EntityVersionUtils.validateEntityVersion(appService, appServiceData.getDataVersionToken(), versionHolder);

        appService.setName(appServiceData.getName());
        appService.setServiceVersion(appServiceData.getServiceVersion());

        return toAppServiceSaveInfo(appService);
    }

    @Override
    public AppServiceDetails updateAppService(long appServiceId, AppServiceUpdateData updateData) throws AppServiceNotFoundException {
        AppService appService = appServiceRepository.findById(appServiceId)
                .orElseThrow(() -> new AppServiceNotFoundException(appServiceId));

        EntityVersionUtils.validateEntityVersion(appService, updateData.getDataVersionToken(), versionHolder);

        updateData.getName().ifPresent(appService::setName);
        updateData.getServiceVersion().ifPresent(appService::setServiceVersion);

        return toAppServiceSaveInfo(appService);
    }

    @Override
    public void removeAppService(long appServiceId) throws AppServiceNotFoundException {
        try {
            appServiceRepository.deleteById(appServiceId);
        }
        catch (EmptyResultDataAccessException e) {
            throw new AppServiceNotFoundException(appServiceId, e);
        }
    }

    private AppServiceDetails toAppServiceSaveInfo(AppService appService) {
        return toAppServiceDetails(appService, null, true);
    }

    private AppServiceDetails toAppServiceDetails(AppService appService, Page<ServiceInstance> topInstances, boolean forUpdate) {
        String versionToken = forUpdate ? versionHolder.storeVersionForUpdate(appService.getVersion()) : null;

        AppServiceDetails.AppServiceDetailsBuilder appServiceBuilder = AppServiceDetails.builder()
                .id(appService.getId())
                .name(appService.getName())
                .serviceVersion(appService.getServiceVersion())
                .dataVersionToken(versionToken);

        if (topInstances != null) {
            appServiceBuilder = appServiceBuilder.instancesInfo(
                    AppServiceInstancesInfo.builder()
                            .totalCount(topInstances.getTotalElements())
                            .topInstances(
                                    topInstances.stream()
                                            .map(this::toServiceInstanceInfo)
                                            .collect(Collectors.toList())
                            )
                            .build()
            );
        }

        return appServiceBuilder.build();
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

    @Override
    public ServiceScaleResult scaleService(long appServiceId, int replication) throws AppServiceNotFoundException {
        if (!appServiceRepository.existsById(appServiceId))
            throw new AppServiceNotFoundException(appServiceId);

        long instancesCount = serviceInstanceRepository.countByServiceId(appServiceId);

        if (instancesCount < replication) {
            int toLaunch = (int) (replication - instancesCount);
            scaleServiceUp(appServiceId, toLaunch);

            return ServiceScaleResult.builder()
                    .startedInstances(replication - instancesCount)
                    .removedInstances(0)
                    .build();
        }

        scaleServiceDown(appServiceId, replication);
        return ServiceScaleResult.builder()
                .startedInstances(0)
                .removedInstances(instancesCount - replication)
                .build();
    }

    private void scaleServiceUp(long appServiceId, int toLaunchNum) {
        List<HostNode>  hostNodes = hostNodeRepository.findLoadBalancedRepositories(PageRequest.of(0, toLaunchNum));
        Map<Long, HostPortResolver> portResolvers = hostNodes.stream()
                .collect(Collectors.toMap(
                        HostNode::getId,
                        host -> new HostPortResolver(host.getUsedPorts())
                ));

        int launched = 0;

        while (launched < toLaunchNum) {
            if (hostNodes.isEmpty())
                throw new IllegalStateException("Failed to replicate service. Not enough number of available ports");

            List<HostNode> toRemove = new ArrayList<>();

            for (HostNode host : hostNodes) {
                HostPortResolver portResolver = portResolvers.get(host.getId());

                try {
                    int port = portResolver.resolveUnusedPort(host);
                    addAppServiceInstance(
                            ServiceInstanceData.builder()
                                    .appServiceId(appServiceId)
                                    .hostNodeId(host.getId())
                                    .port(port)
                                    .build()
                    );

                    launched++;
                }
                catch (BusinessException e) {
                    log.warn("Unexpected business exception when adding new service instance", e);
                }
                catch (IllegalStateException e) {
                    // Port resolver failed to find an unused port
                    toRemove.add(host);
                }
            }

            hostNodes.removeAll(toRemove);
        }
    }

    private void scaleServiceDown(long appServiceId, int replication) {
        serviceInstanceRepository.deleteInBatch(
                serviceInstanceRepository.findAllByServiceId(appServiceId)
                        .skip(replication).collect(Collectors.toList())
        );
    }
}
