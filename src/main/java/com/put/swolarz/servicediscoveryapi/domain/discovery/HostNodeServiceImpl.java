package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.common.PatchUpdateDictionary;
import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.common.util.DtoUtils;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.DataCenterNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeAlreadyExistsException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;


@Service
@Transactional
@RequiredArgsConstructor
class HostNodeServiceImpl implements HostNodeService {

    private final HostNodeRepository hostNodeRepository;
    private final DataCenterRepository dataCenterRepository;


    @Override
    public ResultsPage<HostNodeDetails> getHostNodesPage(int page, int perPage) {
        Pageable pageRequest = PageRequest.of(page, perPage);
        Page<HostNode> resultsPage = hostNodeRepository.findAll(pageRequest);

        return DtoUtils.toDtoResultsPage(resultsPage, pageRequest, this::toHostNodeDetails, HostNodeDetails.class);
    }

    @Override
    public HostNodeDetails getHostNode(long hostNodeId) throws HostNodeNotFoundException {
        HostNode hostNode = hostNodeRepository.findById(hostNodeId)
                .orElseThrow(() -> new HostNodeNotFoundException(hostNodeId));

        return toHostNodeDetails(hostNode);
    }

    @Override
    public HostNodeDetails createHostNode(HostNodeData hostNode) throws HostNodeAlreadyExistsException, DataCenterNotFoundException {
        Long hostNodeId = hostNode.getId();

        if (hostNodeId != null && hostNodeRepository.existsById(hostNodeId)) {
            throw new HostNodeAlreadyExistsException(hostNodeId);
        }

        DataCenter dataCenter = dataCenterRepository.findById(hostNode.getDataCenterId())
                .orElseThrow(() -> new DataCenterNotFoundException(hostNode.getDataCenterId()));

        HostStatus hostStatus = HostStatus.fromValue(hostNode.getStatus());

        HostNode newHostNode = hostNodeRepository.save(
                HostNode.builder()
                        .id(hostNodeId)
                        .name(hostNode.getName())
                        .status(hostStatus)
                        .dataCenter(dataCenter)
                        .os(hostNode.getOperatingSystem())
                        .build()
        );

        return toHostNodeDetails(newHostNode);
    }

    @Override
    public HostNodeDetails updateHostNode(HostNodeData hostNodeDto, long hostNodeId, boolean createIfNotExists)
            throws HostNodeNotFoundException, DataCenterNotFoundException {

        Optional<HostNode> optionalHostNode = hostNodeRepository.findById(hostNodeId);

        if (optionalHostNode.isEmpty()) {
            if (!createIfNotExists)
                throw new HostNodeNotFoundException(hostNodeId);

            try {
                return createHostNode(hostNodeDto.toBuilder().id(hostNodeId).build());
            }
            catch (HostNodeAlreadyExistsException e) {
                // Such situation should never happen, since we check for the existence of the host node
                throw new IllegalStateException("Race condition! The host node was supposed to be non existent");
            }
        }

        DataCenter dataCenter = dataCenterRepository.findById(hostNodeDto.getDataCenterId())
                .orElseThrow(() -> new DataCenterNotFoundException(hostNodeDto.getDataCenterId()));

        HostNode hostNode = optionalHostNode.get();

        hostNode.setName(hostNodeDto.getName());
        hostNode.setStatus(HostStatus.fromValue(hostNodeDto.getStatus()));
        hostNode.setDataCenter(dataCenter);
        hostNode.setOs(hostNodeDto.getOperatingSystem());

        return toHostNodeDetails(hostNode);
    }

    @Override
    public HostNodeDetails updateHostNode(long hostNodeId, PatchUpdateDictionary updateAttributes) {
        throw new UnsupportedOperationException("Not implemented");
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

    private HostNodeDetails toHostNodeDetails(HostNode hostNode) {
        return HostNodeDetails.builder()
                .id(hostNode.getId())
                .version(hostNode.getVersion())
                .name(hostNode.getName())
                .status(hostNode.getStatus().name().toLowerCase())
                .launchedAt(hostNode.getLaunchedAt())
                .dataCenterId(hostNode.getDataCenter().getId())
                .dataCenterName(hostNode.getDataCenter().getName())
                .operatingSystem(hostNode.getOs())
                .build();
    }
}

