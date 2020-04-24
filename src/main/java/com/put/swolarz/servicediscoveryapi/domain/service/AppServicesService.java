package com.put.swolarz.servicediscoveryapi.domain.service;


import com.put.swolarz.servicediscoveryapi.api.dto.common.PatchUpdateDictionary;
import com.put.swolarz.servicediscoveryapi.domain.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.model.common.EntitiesPage;
import com.put.swolarz.servicediscoveryapi.domain.model.discovery.AppService;

import java.util.Optional;

public interface AppServicesService {

    EntitiesPage<AppService> getAppServices(int page, int perPage) throws BusinessException;
    Optional<AppService> getAppService(long appServiceId) throws BusinessException;

    AppService createAppService(AppService appService) throws BusinessException;
    AppService createOrUpdateAppService(AppService appService) throws BusinessException;
    AppService updateAppService(long appServiceId, PatchUpdateDictionary props) throws BusinessException;

    boolean removeAppService(long appServiceId) throws BusinessException;
}
