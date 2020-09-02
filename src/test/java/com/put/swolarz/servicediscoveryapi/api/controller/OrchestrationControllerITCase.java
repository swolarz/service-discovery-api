package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.MigrationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.AppServiceTestUtils.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.DataCenterTestUtils.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.HostNodeTestUtils.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.ServiceInstanceTestUtils.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class OrchestrationControllerITCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;


    @Test
    void testServiceInstancesMigration() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("datacenter", "Beyond");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("host1", "up", "mint", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        HostNodeRequest hostRequest2 = new HostNodeRequest("host2", "up", "kali", dcId);
        long hostId2 = postHostNodeForId(mockMvc, hostRequest2, mapper);

        HostNodeRequest hostRequest3 = new HostNodeRequest("host3", "up", "ubuntu", dcId);
        long hostId3 = postHostNodeForId(mockMvc, hostRequest3, mapper);

        AppServiceRequest serviceRequest = newAppService("postgres", "12");
        long serviceId = postAppServiceForId(mockMvc, serviceRequest, mapper);

        ServiceInstanceRequest instanceRequest1 = new ServiceInstanceRequest(serviceId, hostId, 8080);
        ServiceInstanceRequest instanceRequest2 = new ServiceInstanceRequest(serviceId, hostId, 5000);
        ServiceInstanceRequest instanceRequest3 = new ServiceInstanceRequest(serviceId, hostId2, 80);
        ServiceInstanceRequest instanceRequest4 = new ServiceInstanceRequest(serviceId, hostId3, 5432);

        long instanceId1 = postServiceInstanceForId(mockMvc, instanceRequest1, mapper);
        long instanceId2 = postServiceInstanceForId(mockMvc, instanceRequest2, mapper);
        long instanceId3 = postServiceInstanceForId(mockMvc, instanceRequest3, mapper);
        long instanceId4 = postServiceInstanceForId(mockMvc, instanceRequest4, mapper);

        getHostNode(mockMvc, hostId)
                .andExpect(matchesHostNodeRequest(hostRequest, hostId, dcRequest, 2, mapper));

        getHostNode(mockMvc, hostId2)
                .andExpect(matchesHostNodeRequest(hostRequest2, hostId2, dcRequest, 1, mapper));

        MigrationRequest migrationRequest = new MigrationRequest(hostId, hostId2);
        mockMvc.perform(
                post("/api/orchestration/migration")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(migrationRequest))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.migratedInstances", is(2)));

        getHostNode(mockMvc, hostId)
                .andExpect(matchesHostNodeRequest(hostRequest, hostId, dcRequest, 0, mapper));

        getHostNode(mockMvc, hostId2)
                .andExpect(matchesHostNodeRequest(hostRequest2, hostId2, dcRequest, 3, mapper));

        getServiceInstance(mockMvc, instanceId1, serviceId)
                .andExpect(jsonPath("$.hostNodeId", is(hostId2)));

        getServiceInstance(mockMvc, instanceId2, serviceId)
                .andExpect(jsonPath("$.hostNodeId", is(hostId2)));

        getServiceInstance(mockMvc, instanceId3, serviceId)
                .andExpect(jsonPath("$.hostNodeId", is(hostId2)));

        getServiceInstance(mockMvc, instanceId4, serviceId)
                .andExpect(jsonPath("$.hostNodeId", is(hostId3)));
    }

    @Test
    void testServiceInstancesMigrationWithCollidingPorts() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("datacenter", "Beyond");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("host1", "up", "mint", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        HostNodeRequest hostRequest2 = new HostNodeRequest("host2", "up", "kali", dcId);
        long hostId2 = postHostNodeForId(mockMvc, hostRequest2, mapper);

        AppServiceRequest serviceRequest = newAppService("postgres", "12");
        long serviceId = postAppServiceForId(mockMvc, serviceRequest, mapper);

        ServiceInstanceRequest instanceRequest1 = new ServiceInstanceRequest(serviceId, hostId, 80);
        ServiceInstanceRequest instanceRequest2 = new ServiceInstanceRequest(serviceId, hostId2, 80);

        long instanceId1 = postServiceInstanceForId(mockMvc, instanceRequest1, mapper);
        long instanceId2 = postServiceInstanceForId(mockMvc, instanceRequest2, mapper);

        getHostNode(mockMvc, hostId)
                .andExpect(matchesHostNodeRequest(hostRequest, hostId, dcRequest, 2, mapper));

        getHostNode(mockMvc, hostId2)
                .andExpect(matchesHostNodeRequest(hostRequest2, hostId, dcRequest, 1, mapper));

        MigrationRequest migrationRequest = new MigrationRequest(hostId, hostId2);
        mockMvc.perform(
                post("/api/orchestration/migration")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(migrationRequest))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.migratedInstances", is(2)));

        getHostNode(mockMvc, hostId)
                .andExpect(matchesHostNodeRequest(hostRequest, hostId, dcRequest, 0, mapper));

        getHostNode(mockMvc, hostId2)
                .andExpect(matchesHostNodeRequest(hostRequest2, hostId2, dcRequest, 2, mapper));

        getServiceInstance(mockMvc, instanceId1, serviceId)
                .andExpect(jsonPath("$.hostNodeId", is(hostId2)))
                .andExpect(jsonPath("$.port", not(80)));

        getServiceInstance(mockMvc, instanceId2, serviceId)
                .andExpect(jsonPath("$.hostNodeId", is(hostId2)))
                .andExpect(jsonPath("$.port", is(80)));
    }

    @Test
    void testMigrationWithNotExistingHosts() throws Exception {
        MigrationRequest migrationRequest = new MigrationRequest(1, 2);
        mockMvc.perform(
                post("/api/orchestration/migration")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(migrationRequest))
        )
                .andExpect(status().isNotFound());
    }
}