package com.put.swolarz.servicediscoveryapi.domain.service;

import com.put.swolarz.servicediscoveryapi.domain.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.model.common.EntitiesPage;
import com.put.swolarz.servicediscoveryapi.domain.model.discovery.HostNode;

public interface HostNodeService {

    EntitiesPage<HostNode> getHostNodesPage(int page, int perPage) throws BusinessException;
    HostNode getHostNode(long hostNodeId) throws BusinessException;

    HostNode createHostNode(HostNode initialHostNode) throws BusinessException;
    HostNode updateHostNode(HostNode updatedHostNode) throws BusinessException;
    HostNode createOrUpdateHostNode(HostNode hostNode) throws BusinessException;

    long removeHostNode(long hostNodeId) throws BusinessException;
}
