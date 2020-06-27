package com.put.swolarz.servicediscoveryapi.domain.discovery;


import com.put.swolarz.servicediscoveryapi.domain.common.common.PatchUpdateDictionary;
import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.common.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.*;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.AppServiceAlreadyExistsException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.AppServiceNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.ServiceInstanceNotFoundException;


public interface AppServicesService {

    ResultsPage<AppServiceDetails> getAppServices(int page, int perPage);
    AppServiceDetails getAppService(long appServiceId) throws AppServiceNotFoundException;

    ResultsPage<ServiceInstanceDetails> getAppServiceInstances(long appServiceId, int page, int perPage) throws AppServiceNotFoundException;
    ServiceInstanceDetails getAppServiceInstance(long appServiceId, long serviceInstanceId)
        throws AppServiceNotFoundException, ServiceInstanceNotFoundException;

    ServiceInstanceDetails addAppServiceInstance(ServiceInstanceData serviceInstanceData)
            throws AppServiceNotFoundException, HostNodeNotFoundException;

    void removeAppServiceInstance(long serviceInstanceId, long appServiceId)
            throws AppServiceNotFoundException, ServiceInstanceNotFoundException;

    AppServiceDetails createAppService(AppServiceData appService) throws AppServiceAlreadyExistsException;
    AppServiceDetails updateAppService(AppServiceData appService, boolean create) throws AppServiceNotFoundException;
    AppServiceDetails updateAppService(long appServiceId, PatchUpdateDictionary updateAttributes) throws BusinessException;

    void removeAppService(long appServiceId) throws AppServiceNotFoundException;
}
