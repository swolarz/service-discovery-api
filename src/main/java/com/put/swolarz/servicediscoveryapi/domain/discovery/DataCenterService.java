package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterUpdateData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.DataCenterNotFoundException;


public interface DataCenterService {
    ResultsPage<DataCenterDetails> getDataCenters(int page, int perPage);
    DataCenterDetails getDataCenter(long dataCenterId) throws DataCenterNotFoundException;

    DataCenterDetails createDataCenter(DataCenterData dataCenterData);
    DataCenterDetails updateDataCenter(long dataCenterId, DataCenterData dataCenterData) throws DataCenterNotFoundException;
    DataCenterDetails updateDataCenter(long dataCenterId, DataCenterUpdateData dataCenterUpdateData) throws DataCenterNotFoundException;
    DataCenterDetails createOrUpdateDataCenter(long dataCenterId, DataCenterData dataCenterData);

    void removeDataCenter(long dataCenterId) throws DataCenterNotFoundException;
}
