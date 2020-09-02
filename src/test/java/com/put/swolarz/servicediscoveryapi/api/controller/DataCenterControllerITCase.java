package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
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
class DataCenterControllerITCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;


    @Test
    void testDataCentersPagination() throws Exception {
        DataCenterRequest request1 = new DataCenterRequest("us-east-1", "Ohio");
        DataCenterRequest request2 = new DataCenterRequest("us-east-2", "N. Virginia");
        DataCenterRequest request3 = new DataCenterRequest("dc-europe-1", "London");
        DataCenterRequest request4 = new DataCenterRequest("dc-poland-1", "Warszawa");
        DataCenterRequest request5 = new DataCenterRequest("dc-poland-2", "Poznań");

        long id1 = postDataCenterForId(mockMvc, request1, mapper);
        long id2 = postDataCenterForId(mockMvc, request2, mapper);
        long id3 = postDataCenterForId(mockMvc, request3, mapper);
        long id4 = postDataCenterForId(mockMvc, request4, mapper);
        long id5 = postDataCenterForId(mockMvc, request5, mapper);

        getDataCenters(mockMvc, 1, 10)
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.perPage", is(10)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(5)))
                .andExpect(jsonPath("$.results[*].id", containsInAnyOrder(id1, id2, id3, id4, id5)));

        getDataCenters(mockMvc, 1, 2)
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(2)));

        getDataCenters(mockMvc, 2, 2)
                .andExpect(jsonPath("$.page", is(2)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(2)));

        getDataCenters(mockMvc, 3, 2)
                .andExpect(jsonPath("$.page", is(3)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(1)));
    }

    @Test
    void testDataCenterIsNotFound() throws Exception {
        mockMvc.perform(
                get("/api/datacenters/{id}", 5)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreatingDataCenter() throws Exception {
        DataCenterRequest createRequest = new DataCenterRequest("non-aws-data-center", "Australian Outback");
        long dcId = postDataCenterForId(mockMvc, createRequest, mapper);

        getDataCenter(mockMvc, dcId)
                .andExpect(matchesDataCenterRequest(createRequest, dcId, mapper));
    }

    @Test
    void testCreatingDataCenterWithGivenId() throws Exception {
        DataCenterRequest createRequest = new DataCenterRequest("non-aws-data-center", "Australian Outback");
        final long dcId = 1;

        putDataCenter(mockMvc, createRequest, dcId, mapper);

        getDataCenter(mockMvc, dcId)
                .andExpect(matchesDataCenterRequest(createRequest, dcId, mapper));
    }

    @Test
    void testUpdatingDataCenterWithPost() throws Exception {
        DataCenterRequest createRequest = new DataCenterRequest("non-aws-data-center", "Australian Outback");
        DataCenterDetails dataCenterDetails = readDataCenter(postDataCenter(mockMvc, createRequest, mapper), mapper);
        final long dcId = dataCenterDetails.getId();
        final String versionToken = dataCenterDetails.getDataVersionToken();

        DataCenterRequest updateRequest = new DataCenterRequest("hidden-data-center", "Australian Outback", versionToken);
        postDataCenter(mockMvc, updateRequest, dcId, mapper);

        getDataCenter(mockMvc, dcId)
                .andExpect(matchesDataCenterRequest(updateRequest, dcId, mapper));
    }

    @Test
    void testUpdatingDataCenterWithPut() throws Exception {
        DataCenterRequest createRequest = new DataCenterRequest("non-aws-data-center", "Australian Outback");
        DataCenterDetails dataCenterDetails = readDataCenter(postDataCenter(mockMvc, createRequest, mapper), mapper);
        final long dcId = dataCenterDetails.getId();
        final String versionToken = dataCenterDetails.getDataVersionToken();

        DataCenterRequest updateRequest = new DataCenterRequest("hidden-data-center", "Australian Outback", versionToken);

        putDataCenter(mockMvc, updateRequest, dcId, mapper);

        getDataCenter(mockMvc, dcId)
                .andExpect(matchesDataCenterRequest(updateRequest, dcId, mapper));
    }

    @Test
    void testSubsequentUpdatingOfDataCenter() throws Exception {
        DataCenterRequest createRequest = new DataCenterRequest("non-aws-data-center", "Australian Outback");
        DataCenterDetails createdDc = readDataCenter(postDataCenter(mockMvc, createRequest, mapper), mapper);
        final long dcId = createdDc.getId();

        Map<String, Object> updatePatch = dataCenterPatch(Optional.of("unknown-data-center"), Optional.empty(), createdDc.getDataVersionToken());
        DataCenterRequest patchedData = new DataCenterRequest("unknown-data-center", "Australian Outback");
        DataCenterDetails patchedDc = readDataCenter(
                patchDataCenter(mockMvc, updatePatch, dcId, mapper)
                        .andExpect(matchesDataCenterRequest(patchedData, dcId, mapper)),
                mapper
        );

        DataCenterRequest updateRequest = new DataCenterRequest(
                "aws-data-center", "asia-pacific", patchedDc.getDataVersionToken()
        );
        putDataCenter(mockMvc, updateRequest, dcId, mapper);

        getDataCenter(mockMvc, dcId)
                .andExpect(matchesDataCenterRequest(updateRequest, dcId, mapper));
    }

    @Test
    void testPatchingDataCenter() throws Exception {
        DataCenterRequest createRequest = new DataCenterRequest("serverless-datacenter", "Dnoyeb");
        DataCenterDetails createdDc = readDataCenter(postDataCenter(mockMvc, createRequest, mapper), mapper);
        final long dcId = createdDc.getId();

        Map<String, Object> patchRequest = dataCenterPatch(Optional.of("cloud-datacenter"), Optional.empty(), createdDc.getDataVersionToken());
        DataCenterRequest patchedRequest = new DataCenterRequest("cloud-datacenter", "Dnoyeb");

        patchDataCenter(mockMvc, patchRequest, dcId, mapper)
                .andExpect(matchesDataCenterRequest(patchedRequest, dcId, mapper));

        getDataCenter(mockMvc, dcId)
                .andExpect(matchesDataCenterRequest(patchedRequest, dcId, mapper));
    }

    @Test
    void testDeletingDataCenter() throws Exception {
        DataCenterRequest createRequest = new DataCenterRequest("forgotten datacenter", "who knows?");
        final long dcId = postDataCenterForId(mockMvc, createRequest, mapper);

        mockMvc.perform(delete("/api/datacenters/{id}", dcId))
                .andExpect(status().is2xxSuccessful());

        getDataCenterUnchecked(mockMvc, dcId)
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeletingDataCenterWithHostsAndServiceInstances() throws Exception {
        DataCenterRequest createRequest = new DataCenterRequest("deprecated datacenter", "Atlantis");
        final long dcId = postDataCenterForId(mockMvc, createRequest, mapper);

        HostNodeRequest hostRequest1 = new HostNodeRequest("host1", "up", "mint", dcId);
        HostNodeRequest hostRequest2 = new HostNodeRequest("host2", "up", "kali", dcId);

        long hostId1 = postHostNodeForId(mockMvc, hostRequest1, mapper);
        long hostId2 = postHostNodeForId(mockMvc, hostRequest2, mapper);

        AppServiceRequest serviceRequest = newAppService("geolocation", "1.0.0");
        long serviceId = postAppServiceForId(mockMvc, serviceRequest, mapper);

        ServiceInstanceRequest instanceRequest1 = new ServiceInstanceRequest(serviceId, hostId1, 80);
        ServiceInstanceRequest instanceRequest2 = new ServiceInstanceRequest(serviceId, hostId2, 80);

        long instanceId1 = postServiceInstanceForId(mockMvc, instanceRequest1, mapper);
        long instanceId2 = postServiceInstanceForId(mockMvc, instanceRequest2, mapper);

        mockMvc.perform(delete("/api/datacenters/{id}", dcId))
                .andExpect(status().is2xxSuccessful());

        getDataCenterUnchecked(mockMvc, dcId)
                .andExpect(status().isNotFound());

        getHostNodeUnchecked(mockMvc, hostId1)
                .andExpect(status().isNotFound());

        getHostNodeUnchecked(mockMvc, hostId2)
                .andExpect(status().isNotFound());

        getServiceInstanceUnchecked(mockMvc, instanceId1, serviceId)
                .andExpect(status().isNotFound());

        getServiceInstanceUnchecked(mockMvc, instanceId2, serviceId)
                .andExpect(status().isNotFound());
    }
}
