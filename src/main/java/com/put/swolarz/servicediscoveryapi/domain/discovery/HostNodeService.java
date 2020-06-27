package com.put.swolarz.servicediscoveryapi.domain.discovery;

import com.put.swolarz.servicediscoveryapi.domain.common.common.PatchUpdateDictionary;
import com.put.swolarz.servicediscoveryapi.domain.common.dto.ResultsPage;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeData;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.DataCenterNotFoundException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeAlreadyExistsException;
import com.put.swolarz.servicediscoveryapi.domain.discovery.exception.HostNodeNotFoundException;


public interface HostNodeService {

    ResultsPage<HostNodeDetails> getHostNodesPage(int page, int perPage);
    HostNodeDetails getHostNode(long hostNodeId) throws HostNodeNotFoundException;

    HostNodeDetails createHostNode(HostNodeData hostNode)
            throws HostNodeAlreadyExistsException, DataCenterNotFoundException;

    HostNodeDetails updateHostNode(HostNodeData hostNode, long hostNodeId, boolean createIfNotExists)
            throws HostNodeNotFoundException, DataCenterNotFoundException;

    HostNodeDetails updateHostNode(long hostNodeId, PatchUpdateDictionary updateAttributes)
            throws HostNodeNotFoundException, DataCenterNotFoundException;

    void removeHostNode(long hostNodeId) throws HostNodeNotFoundException;
}
