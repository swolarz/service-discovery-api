package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.data.ReadOnlyTransaction;
import com.put.swolarz.servicediscoveryapi.domain.common.data.ReadWriteTransaction;
import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.common.util.DtoUtils;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterUpdateData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.DataCenterNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;


@Service
@ReadWriteTransaction
@RequiredArgsConstructor
class DataCenterServiceImpl implements DataCenterService {

    private final DataCenterRepository dataCenterRepository;


    @Override
    @ReadOnlyTransaction
    public ResultsPage<DataCenterDetails> getDataCenters(int page, int perPage) {
        PageRequest pageRequest = PageRequest.of(page, perPage);
        Page<DataCenter> dataCentersPage = dataCenterRepository.findAll(pageRequest);

        return DtoUtils.toDtoResultsPage(dataCentersPage, pageRequest, this::toDataCenterDetails, DataCenterDetails.class);
    }

    @Override
    @ReadOnlyTransaction
    public DataCenterDetails getDataCenter(long dataCenterId) throws DataCenterNotFoundException {
        DataCenter dataCenter = dataCenterRepository.findById(dataCenterId)
                .orElseThrow(() -> new DataCenterNotFoundException(dataCenterId));

        return toDataCenterDetails(dataCenter);
    }

    @Override
    public DataCenterDetails createDataCenter(DataCenterData dataCenterData) {
        DataCenter dataCenter = dataCenterRepository.save(
                new DataCenter(dataCenterData.getName(), dataCenterData.getLocation())
        );

        return toDataCenterDetails(dataCenter);
    }

    @Override
    public DataCenterDetails updateDataCenter(long dataCenterId, DataCenterData dataCenterData) throws DataCenterNotFoundException {
        DataCenter dataCenter = dataCenterRepository.findById(dataCenterId)
                .orElseThrow(() -> new DataCenterNotFoundException(dataCenterId));

        return toDataCenterDetails(makeDataCenterUpdate(dataCenter, dataCenterData));
    }

    @Override
    public DataCenterDetails updateDataCenter(long dataCenterId, DataCenterUpdateData dataCenterUpdateData)
            throws DataCenterNotFoundException {

        DataCenter dataCenter = dataCenterRepository.findById(dataCenterId)
                .orElseThrow(() -> new DataCenterNotFoundException(dataCenterId));

        dataCenterUpdateData.getName().ifPresent(dataCenter::setName);

        return toDataCenterDetails(dataCenter);
    }

    @Override
    public DataCenterDetails createOrUpdateDataCenter(long dataCenterId, DataCenterData dataCenterData) {
        DataCenter dataCenter = dataCenterRepository.findById(dataCenterId)
                .map(dc -> makeDataCenterUpdate(dc, dataCenterData))
                .orElseGet(() ->
                        dataCenterRepository.save(
                                new DataCenter(dataCenterData.getName(), dataCenterData.getLocation())
                        )
                );

        return toDataCenterDetails(dataCenter);
    }

    private DataCenter makeDataCenterUpdate(DataCenter dataCenter, DataCenterData dataCenterData) {
        dataCenter.setName(dataCenterData.getName());
        dataCenter.setLocation(dataCenterData.getLocation());

        return dataCenter;
    }

    @Override
    public void removeDataCenter(long dataCenterId) throws DataCenterNotFoundException {
        try {
            dataCenterRepository.deleteById(dataCenterId);
        }
        catch (EmptyResultDataAccessException e) {
            throw new DataCenterNotFoundException(dataCenterId, e);
        }
    }

    private DataCenterDetails toDataCenterDetails(DataCenter dataCenter) {
        return DataCenterDetails.builder()
                .id(dataCenter.getId())
                .name(dataCenter.getName())
                .location(dataCenter.getLocation())
                .build();
    }
}
