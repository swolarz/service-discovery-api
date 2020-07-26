package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeUpdateData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.DataCenterNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeNotFoundException;


public interface HostNodeService {

    ResultsPage<HostNodeDetails> getHostNodesPage(int page, int perPage);
    HostNodeDetails getHostNode(long hostNodeId) throws HostNodeNotFoundException;

    HostNodeDetails createHostNode(HostNodeData hostNode) throws DataCenterNotFoundException;
    HostNodeDetails createOrUpdateHostNode(long hostNodeId, HostNodeData hostNode) throws DataCenterNotFoundException;

    HostNodeDetails updateHostNode(long hostNodeId, HostNodeData hostNode)
            throws HostNodeNotFoundException, DataCenterNotFoundException;

    HostNodeDetails updateHostNode(long hostNodeId, HostNodeUpdateData updateAttributes)
            throws HostNodeNotFoundException, DataCenterNotFoundException;

    void removeHostNode(long hostNodeId) throws HostNodeNotFoundException;
}
