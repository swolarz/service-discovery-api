package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;


import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.DataCenterTestUtils.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.HostNodeTestUtils.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.AppServiceTestUtils.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.ServiceInstanceTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class HostNodeControllerITCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    void testHostNodesPagination() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("us-east-1", "Ohio");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest request1 = new HostNodeRequest("host-a", "up", "ubuntu", dcId);
        HostNodeRequest request2 = new HostNodeRequest("host-a", "down", "ubuntu", dcId);
        HostNodeRequest request3 = new HostNodeRequest("host-a", "restarting", "ubuntu", dcId);
        HostNodeRequest request4 = new HostNodeRequest("host-a", "up", "ubuntu", dcId);
        HostNodeRequest request5 = new HostNodeRequest("host-a", "up", "ubuntu", dcId);

        long id1 = postHostNodeForId(mockMvc, request1, mapper);
        long id2 = postHostNodeForId(mockMvc, request2, mapper);
        long id3 = postHostNodeForId(mockMvc, request3, mapper);
        long id4 = postHostNodeForId(mockMvc, request4, mapper);
        long id5 = postHostNodeForId(mockMvc, request5, mapper);

        getHostNodes(mockMvc, 1, 10)
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.perPage", is(10)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(5)))
                .andExpect(jsonPath("$.results[*].id", containsInAnyOrder(id1, id2, id3, id4, id5)));

        getHostNodes(mockMvc, 1, 2)
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(2)));

        getHostNodes(mockMvc, 2, 2)
                .andExpect(jsonPath("$.page", is(2)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(2)));

        getHostNodes(mockMvc, 3, 2)
                .andExpect(jsonPath("$.page", is(3)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(1)));
    }

    @Test
    void testHostNodeIsNotFound() throws Exception {
        getHostNodeUnchecked(mockMvc, 5)
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatingHostNode() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("non-aws-data-center", "Australian Outback");
        long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest createRequest = new HostNodeRequest("new-node", "up", "redhat", dcId);

        ResultActions resultActions = postHostNode(mockMvc, createRequest, mapper)
                .andExpect(matchesHostNodeRequest(createRequest, null, dcRequest, 0, mapper));

        HostNodeDetails hostNodeDetails = readHostNode(resultActions, mapper);

        getHostNode(mockMvc, hostNodeDetails.getId())
                .andExpect(matchesHostNodeRequest(createRequest, dcId, dcRequest, 0, mapper));
    }

    @Test
    void testCreatingHostNodeWithGivenId() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("non-aws-data-center", "Australian Outback");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest createRequest = new HostNodeRequest("new-numbered-node", "stopped", "centos", dcId);
        final long hostId = 1;

        putHostNode(mockMvc, createRequest, hostId, mapper)
                .andExpect(matchesHostNodeRequest(createRequest, hostId, dcRequest, 0, mapper));

        getHostNode(mockMvc, hostId)
                .andExpect(matchesHostNodeRequest(createRequest, dcId, dcRequest, 0, mapper));
    }

    @Test
    void testUpdatingHostNodeWithPost() throws Exception {
        DataCenterRequest dcRequest= new DataCenterRequest("non-aws-data-center", "Australian Outback");
        long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest createRequest = new HostNodeRequest("typo-node", "restarting", "50centos", dcId);
        HostNodeDetails hostNodeDetails = readHostNode(postHostNode(mockMvc, createRequest, mapper), mapper);
        final long hostId = hostNodeDetails.getId();
        final String versionToken = hostNodeDetails.getDataVersionToken();

        HostNodeRequest correctRequest = new HostNodeRequest("correct-node", "up", "50cent", dcId, versionToken);
        putHostNode(mockMvc, correctRequest, hostId, mapper)
                .andExpect(matchesHostNodeRequest(correctRequest, hostId, dcRequest, 0, mapper));

        getHostNode(mockMvc, hostId)
                .andExpect(matchesHostNodeRequest(correctRequest, hostId, dcRequest, 0, mapper));
    }

    @Test
    void testUpdatingHostNodeWithPut() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("non-aws-data-center", "Australian Outback");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        DataCenterRequest alternativeDcRequest = new DataCenterRequest("us-east-1", "Ohio");
        final long aDcId = postDataCenterForId(mockMvc, alternativeDcRequest, mapper);

        HostNodeRequest createRequest = new HostNodeRequest("test node", "up", "debian", dcId);
        HostNodeDetails hostNodeDetails = readHostNode(postHostNode(mockMvc, createRequest, mapper), mapper);
        final long hostId = hostNodeDetails.getId();
        final String versionToken = hostNodeDetails.getDataVersionToken();

        HostNodeRequest updateRequest = new HostNodeRequest("test node", "up", "ubuntu", aDcId, versionToken);
        putHostNode(mockMvc, updateRequest, hostId, mapper);

        getHostNode(mockMvc, dcId)
                .andExpect(matchesHostNodeRequest(updateRequest, dcId, alternativeDcRequest, 0, mapper));
    }

    @Test
    void testSubsequentUpdatingOfHostNode() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("non-aws-data-center", "Australian Outback");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest createRequest = new HostNodeRequest("unclear node", "up", "arch", dcId);
        HostNodeDetails createdHost = readHostNode(postHostNode(mockMvc, createRequest, mapper), mapper);
        final long hostId = createdHost.getId();
        final String versionToken1 = createdHost.getDataVersionToken();

        Map<String, Object> updatePatch = hostNodePatch(
                Optional.of("fixed node"), Optional.empty(), Optional.of("Arch"), Optional.empty(), versionToken1
        );
        HostNodeRequest patchedData = new HostNodeRequest("fixed node", "up", "Arch", dcId);

        DataCenterDetails patchedDc = readDataCenter(
                patchDataCenter(mockMvc, updatePatch, dcId, mapper)
                        .andExpect(matchesHostNodeRequest(patchedData, hostId, dcRequest, 0, mapper)),
                mapper
        );

        HostNodeRequest updateRequest = new HostNodeRequest(
                "complete node", "stopped", "Kali", dcId, patchedDc.getDataVersionToken()
        );
        putHostNode(mockMvc, updateRequest, hostId, mapper)
                .andExpect(matchesHostNodeRequest(updateRequest, hostId, dcRequest, 0, mapper));

        getDataCenter(mockMvc, dcId)
                .andExpect(matchesHostNodeRequest(updateRequest, hostId, dcRequest, 0, mapper));
    }

    @Test
    void testPatchingDataCenter() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("serverless-datacenter", "beynd");
        long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        DataCenterRequest dcRequest2 = new DataCenterRequest("serverful-datacenter", "amazon");
        long dcId2 = postDataCenterForId(mockMvc, dcRequest2, mapper);

        HostNodeRequest createRequest = new HostNodeRequest("unpatched-host", "up", "wyndows", dcId);
        HostNodeDetails createdHost = readHostNode(postHostNode(mockMvc, createRequest, mapper), mapper);

        Map<String, Object> patchRequest = hostNodePatch(
                Optional.of("host"), Optional.empty(), Optional.of("windows"), Optional.of(dcId2), createdHost.getDataVersionToken()
        );
        HostNodeRequest patchedHost = new HostNodeRequest("host", "up", "windows", dcId2);

        patchHostNode(mockMvc, patchRequest, dcId, mapper)
                .andExpect(matchesHostNodeRequest(patchedHost, createdHost.getId(), dcRequest2, 0, mapper));

        getHostNode(mockMvc, createdHost.getId())
                .andExpect(matchesHostNodeRequest(patchedHost, createdHost.getId(), dcRequest2, 0, mapper));
    }

    @Test
    void testDeletingDataCenter() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("forgotten datacenter", "who knows?");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest createRequest = new HostNodeRequest("forgotten host", "up", "os", dcId);
        final long hostId = postHostNodeForId(mockMvc, createRequest, mapper);

        mockMvc.perform(delete("/api/hosts/{id}", hostId))
                .andExpect(status().is2xxSuccessful());

        getHostNodeUnchecked(mockMvc, hostId)
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletingDataCenterWithHostsAndServiceInstances() throws Exception {
        DataCenterRequest createRequest = new DataCenterRequest("deprecated datacenter", "Atlantis");
        final long dcId = postDataCenterForId(mockMvc, createRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("host1", "up", "mint", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        HostNodeRequest hostRequest2 = new HostNodeRequest("host2", "up", "kali", dcId);
        long hostId2 = postHostNodeForId(mockMvc, hostRequest2, mapper);

        AppServiceRequest serviceRequest = newAppService("geolocation", "1.0.0");
        long serviceId = postAppServiceForId(mockMvc, serviceRequest, mapper);

        ServiceInstanceRequest instanceRequest1 = new ServiceInstanceRequest(serviceId, hostId, 8080);
        ServiceInstanceRequest instanceRequest2 = new ServiceInstanceRequest(serviceId, hostId, 5000);
        ServiceInstanceRequest instanceRequest3 = new ServiceInstanceRequest(serviceId, hostId2, 80);

        long instanceId1 = postServiceInstanceForId(mockMvc, instanceRequest1, mapper);
        long instanceId2 = postServiceInstanceForId(mockMvc, instanceRequest2, mapper);
        long instanceId3 = postServiceInstanceForId(mockMvc, instanceRequest3, mapper);

        getHostNode(mockMvc, hostId)
                .andExpect(matchesHostNodeRequest(hostRequest, hostId, createRequest, 2, mapper));

        getAppServices(mockMvc, 1, 10)
                .andExpect(jsonPath("$.results", hasSize(3)));

        mockMvc.perform(delete("/api/hosts/{id}", dcId))
                .andExpect(status().is2xxSuccessful());

        getHostNodeUnchecked(mockMvc, hostId)
                .andExpect(status().isNotFound());

        getServiceInstanceUnchecked(mockMvc, instanceId1, serviceId)
                .andExpect(status().isNotFound());

        getServiceInstanceUnchecked(mockMvc, instanceId2, serviceId)
                .andExpect(status().isNotFound());

        getAppServices(mockMvc, 1, 10)
                .andExpect(jsonPath("$.results", hasSize(1)))
                .andExpect(jsonPath("$.results[0].id", is(instanceId3)));
    }
}
