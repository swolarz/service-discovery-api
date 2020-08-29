package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.ReadOnlyTransaction;
import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.common.util.DtoUtils;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeUpdateData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.DataCenterNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.websync.OptimisticVersionHolder;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@ReadOnlyTransaction
@RequiredArgsConstructor
class HostNodeServiceImpl implements HostNodeService {

    private final HostNodeRepository hostNodeRepository;
    private final DataCenterRepository dataCenterRepository;
    private final ServiceInstanceRepository serviceInstanceRepository;

    private final OptimisticVersionHolder versionHolder;


    @Override
    public ResultsPage<HostNodeDetails> getHostNodesPage(int page, int perPage) {
        Pageable pageRequest = PageRequest.of(page, perPage);
        Page<HostNode> resultsPage = hostNodeRepository.findAll(pageRequest);

        return DtoUtils.toDtoResultsPage(resultsPage, pageRequest, this::makeHostNodeInfoDetails, HostNodeDetails.class);
    }

    @Override
    public HostNodeDetails getHostNode(long hostNodeId) throws HostNodeNotFoundException {
        HostNode hostNode = hostNodeRepository.findById(hostNodeId)
                .orElseThrow(() -> new HostNodeNotFoundException(hostNodeId));

        return makeHostNodeDetails(hostNode, true);
    }

    @Override
    public HostNodeDetails createHostNode(HostNodeData hostNode) throws DataCenterNotFoundException {
        HostNode newHostNode = hostNodeRepository.save(makeNewHostNode(hostNode));
        return toHostNodeDetails(newHostNode, 0, true);
    }

    @Override
    public HostNodeDetails createOrUpdateHostNode(long hostNodeId, HostNodeData hostNodeData) throws DataCenterNotFoundException {
        HostStatus hostStatus = HostStatus.fromValue(hostNodeData.getStatus());
        DataCenter dataCenter = dataCenterRepository.findById(hostNodeData.getDataCenterId())
                .orElseThrow(() -> new DataCenterNotFoundException(hostNodeData.getDataCenterId()));

        HostNode hostNode = hostNodeRepository.findById(hostNodeId)
                .map(host -> {
                    EntityVersionUtils.validateEntityVersion(host, hostNodeData.getDataVersionToken(), versionHolder);

                    host.setName(hostNodeData.getName());
                    host.setStatus(hostStatus);
                    host.setDataCenter(dataCenter);
                    host.setOs(hostNodeData.getOperatingSystem());

                    return host;
                })
                .orElseGet(
                        () -> hostNodeRepository.save(makeNewHostNode(hostNodeData, dataCenter))
                );

        return makeHostNodeDetails(hostNode, true);
    }

    private HostNode makeNewHostNode(HostNodeData hostNodeData, DataCenter hostDataCenter) {
        HostStatus hostStatus = HostStatus.fromValue(hostNodeData.getStatus());

        return HostNode.builder()
                .name(hostNodeData.getName())
                .status(hostStatus)
                .dataCenter(hostDataCenter)
                .os(hostNodeData.getOperatingSystem())
                .build();
    }

    private HostNode makeNewHostNode(HostNodeData hostNodeData) throws DataCenterNotFoundException {
        DataCenter dataCenter = dataCenterRepository.findById(hostNodeData.getDataCenterId())
                .orElseThrow(() -> new DataCenterNotFoundException(hostNodeData.getDataCenterId()));

        return makeNewHostNode(hostNodeData, dataCenter);
    }

    @Override
    public HostNodeDetails updateHostNode(long hostNodeId, HostNodeData hostNodeData)
            throws HostNodeNotFoundException, DataCenterNotFoundException {

        HostNode hostNode = hostNodeRepository.findById(hostNodeId)
                .orElseThrow(() -> new HostNodeNotFoundException(hostNodeId));

        EntityVersionUtils.validateEntityVersion(hostNode, hostNodeData.getDataVersionToken(), versionHolder);

        hostNode.setName(hostNodeData.getName());
        hostNode.setStatus(HostStatus.fromValue(hostNodeData.getStatus()));
        hostNode.setOs(hostNodeData.getOperatingSystem());
        updateHostDataCenter(hostNode, hostNodeData.getDataCenterId());

        return makeHostNodeDetails(hostNode, true);
    }

    @Override
    public HostNodeDetails updateHostNode(long hostNodeId, HostNodeUpdateData updateAttributes)
            throws HostNodeNotFoundException, DataCenterNotFoundException {

        HostNode hostNode = hostNodeRepository.findById(hostNodeId)
                .orElseThrow(() -> new HostNodeNotFoundException(hostNodeId));

        EntityVersionUtils.validateEntityVersion(hostNode, updateAttributes.getDataVersionToken(), versionHolder);

        updateAttributes.getName().ifPresent(hostNode::setName);
        updateAttributes.getStatus().map(HostStatus::fromValue).ifPresent(hostNode::setStatus);
        updateAttributes.getOperatingSystem().ifPresent(hostNode::setOs);

        if (updateAttributes.getDataCenterId().isPresent()) {
            updateHostDataCenter(hostNode, updateAttributes.getDataCenterId().get());
        }

        return makeHostNodeDetails(hostNode, true);
    }

    private void updateHostDataCenter(HostNode hostNode, long newDataCenterId) throws DataCenterNotFoundException {
        if (!hostNode.getDataCenter().getId().equals(newDataCenterId)) {
            DataCenter dataCenter = dataCenterRepository.findById(newDataCenterId)
                    .orElseThrow(() -> new DataCenterNotFoundException(newDataCenterId));

            hostNode.setDataCenter(dataCenter);
        }
    }

    @Override
    public void removeHostNode(long hostNodeId) throws HostNodeNotFoundException {
        try {
            hostNodeRepository.deleteById(hostNodeId);
        }
        catch (EmptyResultDataAccessException e) {
            throw new HostNodeNotFoundException(hostNodeId, e);
        }
    }

    private HostNodeDetails makeHostNodeInfoDetails(HostNode hostNode) {
        return makeHostNodeDetails(hostNode, false);
    }

    private HostNodeDetails makeHostNodeDetails(HostNode hostNode, boolean forUpdate) {
        int launchedInstances = getLaunchedInstancesCount(hostNode.getId());
        return toHostNodeDetails(hostNode, launchedInstances, forUpdate);
    }

    private int getLaunchedInstancesCount(long hostNodeId) {
        return serviceInstanceRepository.countByHostId(hostNodeId);
    }

    private HostNodeDetails toHostNodeDetails(HostNode hostNode, int launchedInstances, boolean forUpdate) {
        String versionToken = forUpdate ? versionHolder.storeVersionForUpdate(hostNode.getVersion()) : null;

        return HostNodeDetails.builder()
                .id(hostNode.getId())
                .name(hostNode.getName())
                .status(hostNode.getStatus().name().toLowerCase())
                .launchedAt(hostNode.getLaunchedAt())
                .dataCenterId(hostNode.getDataCenter().getId())
                .dataCenterName(hostNode.getDataCenter().getName())
                .operatingSystem(hostNode.getOs())
                .launchedInstances(launchedInstances)
                .dataVersionToken(versionToken)
                .build();
    }
}
