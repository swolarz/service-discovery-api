package com.put.swolarz.servicediscoveryapi.domain.service.impl;

import com.put.swolarz.servicediscoveryapi.domain.exception.BusinessException;
import com.put.swolarz.servicediscoveryapi.domain.exception.ErrorCode;
import com.put.swolarz.servicediscoveryapi.domain.model.common.EntitiesPage;
import com.put.swolarz.servicediscoveryapi.domain.model.discovery.HostNode;
import com.put.swolarz.servicediscoveryapi.domain.repository.HostNodeRepository;
import com.put.swolarz.servicediscoveryapi.domain.service.HostNodeService;
import com.put.swolarz.servicediscoveryapi.domain.service.util.QueryUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
class HostNodeServiceImpl implements HostNodeService {

    private final HostNodeRepository hostNodeRepository;


    @Override
    public EntitiesPage<HostNode> getHostNodesPage(int page, int perPage) throws BusinessException {
        Pageable pageRequest = QueryUtils.createPageRequest(page, perPage);
        Page<HostNode> resultsPage = hostNodeRepository.findAll(pageRequest);

        return QueryUtils.wrapEntitiesPage(resultsPage);
    }

    @Override
    public HostNode getHostNode(long hostNodeId) throws BusinessException {
        return hostNodeRepository.findById(hostNodeId).orElseThrow(
                () -> new BusinessException(ErrorCode.HOST_NODE_NOT_FOUND)
        );
    }

    @Override
    public HostNode createHostNode(HostNode hostNode) throws BusinessException {
        try {
            return hostNodeRepository.saveAndFlush(hostNode);
        }
        catch (DataIntegrityViolationException e) {
            throw new BusinessException(ErrorCode.HOST_NODE_ALREADY_EXISTS, e);
        }
    }

    @Override
    public HostNode updateHostNode(HostNode updatedHostNode) throws BusinessException {
        if (updatedHostNode.getId() == null)
            throw new BusinessException(ErrorCode.ENTITY_ID_NOT_SPECIFIED);

        return hostNodeRepository.save(updatedHostNode);
    }

    @Override
    public HostNode createOrUpdateHostNode(HostNode hostNode) throws BusinessException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public long removeHostNode(long hostNodeId) throws BusinessException {
        HostNode hostNode = hostNodeRepository.getOne(hostNodeId);
        hostNodeRepository.delete(hostNode);

        return hostNodeId;
    }
}
