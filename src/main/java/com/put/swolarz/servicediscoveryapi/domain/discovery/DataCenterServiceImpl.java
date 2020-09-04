package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.ReadOnlyTransaction;
import com.put.swolarz.servicediscoveryapi.domain.common.data.ReadWriteTransaction;
import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.common.util.DtoUtils;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterUpdateData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.DataCenterNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.websync.OptimisticVersionHolder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
@ReadWriteTransaction
@RequiredArgsConstructor
@Slf4j
class DataCenterServiceImpl implements DataCenterService {

    private final DataCenterRepository dataCenterRepository;
    private final OptimisticVersionHolder versionHolder;

    private final HostNodeService hostNodeService;
    private final HostNodeRepository hostNodeRepository;


    @Override
    @ReadOnlyTransaction
    public ResultsPage<DataCenterDetails> getDataCenters(int page, int perPage) {
        PageRequest pageRequest = PageRequest.of(page - 1, perPage);
        Page<DataCenter> dataCentersPage = dataCenterRepository.findAll(pageRequest);

        return DtoUtils.toDtoResultsPage(dataCentersPage, page, perPage, this::toDataCenterInfoDetails, DataCenterDetails.class);
    }

    @Override
    @ReadOnlyTransaction
    public DataCenterDetails getDataCenter(long dataCenterId) throws DataCenterNotFoundException {
        DataCenter dataCenter = dataCenterRepository.findById(dataCenterId)
                .orElseThrow(() -> new DataCenterNotFoundException(dataCenterId));

        return toDataCenterDetails(dataCenter, true);
    }

    @Override
    public DataCenterDetails createDataCenter(DataCenterData dataCenterData) {
        DataCenter dataCenter = dataCenterRepository.saveAndFlush(
                new DataCenter(dataCenterData.getName(), dataCenterData.getLocation())
        );

        return toDataCenterDetails(dataCenter, true);
    }

    @Override
    public DataCenterDetails updateDataCenter(long dataCenterId, DataCenterData dataCenterData) throws DataCenterNotFoundException {
        DataCenter dataCenter = dataCenterRepository.findById(dataCenterId)
                .orElseThrow(() -> new DataCenterNotFoundException(dataCenterId));

        dataCenter = makeDataCenterUpdate(dataCenter, dataCenterData);
        dataCenter = dataCenterRepository.saveAndFlush(dataCenter);

        return toDataCenterDetails(dataCenter, true);
    }

    @Override
    public DataCenterDetails updateDataCenter(long dataCenterId, DataCenterUpdateData dataCenterUpdateData)
            throws DataCenterNotFoundException {

        DataCenter dataCenter = dataCenterRepository.findById(dataCenterId)
                .orElseThrow(() -> new DataCenterNotFoundException(dataCenterId));

        EntityVersionUtils.validateEntityVersion(dataCenter, dataCenterUpdateData.getDataVersionToken(), versionHolder);

        dataCenterUpdateData.getName().ifPresent(dataCenter::setName);

        dataCenter = dataCenterRepository.saveAndFlush(dataCenter);
        return toDataCenterDetails(dataCenter, true);
    }

    @Override
    public DataCenterDetails createOrUpdateDataCenter(long dataCenterId, DataCenterData dataCenterData) {
        DataCenter dataCenter = dataCenterRepository.findById(dataCenterId)
                .map(dc -> makeDataCenterUpdate(dc, dataCenterData))
                .orElseGet(() -> new DataCenter(dataCenterId, dataCenterData.getName(), dataCenterData.getLocation()));

        dataCenter = dataCenterRepository.saveAndFlush(dataCenter);

        return toDataCenterDetails(dataCenter, true);
    }

    private DataCenter makeDataCenterUpdate(DataCenter dataCenter, DataCenterData dataCenterData) {
        EntityVersionUtils.validateEntityVersion(dataCenter, dataCenterData.getDataVersionToken(), versionHolder);

        dataCenter.setName(dataCenterData.getName());
        dataCenter.setLocation(dataCenterData.getLocation());

        return dataCenter;
    }

    @Override
    public void removeDataCenter(long dataCenterId) throws DataCenterNotFoundException {
        DataCenter dataCenter = dataCenterRepository.findById(dataCenterId)
                .orElseThrow(() -> new DataCenterNotFoundException(dataCenterId));

//        dataCenter.getHosts().stream()
        hostNodeRepository.findAllByDataCenterId(dataCenterId)
                .map(HostNode::getId)
                .forEach(hostId -> {
                    try {
                        hostNodeService.removeHostNode(hostId);
                    }
                    catch (HostNodeNotFoundException e) {
                        log.warn(String.format("Unable to remove data center's (id = %d) host node with id = %d", dataCenterId, hostId), e);
                    }
                });

        dataCenterRepository.delete(dataCenter);
    }

    private DataCenterDetails toDataCenterInfoDetails(DataCenter dataCenter) {
        return toDataCenterDetails(dataCenter, false);
    }

    private DataCenterDetails toDataCenterDetails(DataCenter dataCenter, boolean forUpdate) {
        String versionToken = forUpdate ? versionHolder.storeVersionForUpdate(dataCenter.getVersion()) : null;

        return DataCenterDetails.builder()
                .id(dataCenter.getId())
                .name(dataCenter.getName())
                .location(dataCenter.getLocation())
                .dataVersionToken(versionToken)
                .build();
    }
}
