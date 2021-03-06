package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.AppServiceDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeDetails;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.ServiceInstanceDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import javax.transaction.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.AppServiceTestUtils.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.DataCenterTestUtils.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.HostNodeTestUtils.*;
import static com.put.swolarz.servicediscoveryapi.api.controller.ServiceInstanceTestUtils.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AppServiceControllerITCase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;


    @Test
    void testLoadingCreatedServices() throws Exception {
        AppServiceRequest request1 = newAppService("svc1", "1.0");
        AppServiceRequest request2 = newAppService("servajs", "2.0");
        AppServiceRequest request3 = newAppService("niemogepodniesctegoservajsu", "4.0");
        AppServiceRequest request4 = newAppService("mowisieserwis", "5.1");

        postAppService(mockMvc, request1, mapper);
        postAppService(mockMvc, request2, mapper);
        postAppService(mockMvc, request3, mapper);
        postAppService(mockMvc, request4, mapper);

        String responseJson = getAppServices(mockMvc, 1, 10)
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.perPage", is(10)))
                .andExpect(jsonPath("$.totalNumber", is(4)))
                .andExpect(jsonPath("$.results", hasSize(4)))
                .andReturn().getResponse().getContentAsString();

        AppServiceResults response = mapper.readValue(responseJson, AppServiceResults.class);
        Map<String, AppServiceDetails> responseMap = response.getResults().stream()
                .collect(
                        Collectors.toMap(
                                AppServiceDetails::getName,
                                Function.identity()
                        )
                );

        Stream.of(request1, request2, request3, request4)
                .forEach(request -> {
                    assertThat(responseMap, hasKey(request.getName()));
                    assertAppServiceRequestMatches(request, responseMap.get(request.getName()), false);
                });
    }

    @Test
    void testLoadingServicesPage() throws Exception {
        AppServiceRequest request1 = newAppService("svc1", "1.0");
        AppServiceRequest request2 = newAppService("servajs", "2.0");
        AppServiceRequest request3 = newAppService("niemogepodniesctegoservajsu", "4.0");
        AppServiceRequest request4 = newAppService("mowisieserwis", "5.1");
        AppServiceRequest request5 = newAppService("the last service", "9.9999");

        postAppService(mockMvc, request1, mapper);
        postAppService(mockMvc, request2, mapper);
        postAppService(mockMvc, request3, mapper);
        postAppService(mockMvc, request4, mapper);
        postAppService(mockMvc, request5, mapper);

        getAppServices(mockMvc, 1, 3)
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.perPage", is(3)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(3)));

        getAppServices(mockMvc, 2, 3)
                .andExpect(jsonPath("$.page", is(2)))
                .andExpect(jsonPath("$.perPage", is(3)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(2)));

        getAppServices(mockMvc, 3, 2)
                .andExpect(jsonPath("$.page", is(3)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalNumber", is(5)))
                .andExpect(jsonPath("$.results", hasSize(1)));
    }

    @Test
    void testGetAppServiceInstancesPagination() throws Exception {
        AppServiceRequest appServiceRequest = newAppService("service in use", "0.9.0");
        long serviceId = postAppServiceForId(mockMvc, appServiceRequest, mapper);

        DataCenterRequest dcRequest = new DataCenterRequest("us-east-2", "Ohio");
        long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("vm-1", "up", "centos", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        ServiceInstanceRequest instanceRequest80 = new ServiceInstanceRequest(serviceId, hostId, 80);
        ServiceInstanceRequest instanceRequest443 = new ServiceInstanceRequest(serviceId, hostId, 443);
        ServiceInstanceRequest instanceRequest8080 = new ServiceInstanceRequest(serviceId, hostId, 8080);

        long instanceId80 = postServiceInstanceForId(mockMvc, instanceRequest80, mapper);
        long instanceId443 = postServiceInstanceForId(mockMvc, instanceRequest443, mapper);
        long instanceId8080 = postServiceInstanceForId(mockMvc, instanceRequest8080, mapper);

        getHostNode(mockMvc, hostId)
                .andExpect(jsonPath("$.launchedInstances", is(3)));

        getServiceInstances(mockMvc, serviceId, 1, 10)
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.perPage", is(10)))
                .andExpect(jsonPath("$.totalNumber", is(3)))
                .andExpect(jsonPath("$.results", hasSize(3)))
                .andExpect(
                        jsonPath(
                                "$.results[*].id",
                                containsInAnyOrder((int) instanceId80, (int) instanceId443, (int) instanceId8080)
                        )
                );

        getServiceInstances(mockMvc, serviceId, 1, 2)
                .andExpect(jsonPath("$.page", is(1)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalNumber", is(3)))
                .andExpect(jsonPath("$.results", hasSize(2)));

        getServiceInstances(mockMvc, serviceId, 2, 2)
                .andExpect(jsonPath("$.page", is(2)))
                .andExpect(jsonPath("$.perPage", is(2)))
                .andExpect(jsonPath("$.totalNumber", is(3)))
                .andExpect(jsonPath("$.results", hasSize(1)));
    }

    @Test
    void testRetrievingInstanceWithNotExistentAppService() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("us-east-2", "Ohio");
        long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("vm-1", "up", "centos", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        final long missingServiceId = 0;
        ServiceInstanceRequest request = new ServiceInstanceRequest(missingServiceId, hostId, 80);

        postServiceInstanceUnchecked(mockMvc, request, mapper)
                .andExpect(status().isNotFound());
    }

    @Test
    void testRetrievingInstanceWithNotExistentHostNode() throws Exception {
        final long missingHostId = 0;

        AppServiceRequest serviceRequest = newAppService("abstract service", "0.0.0");
        final long serviceId = postAppServiceForId(mockMvc, serviceRequest, mapper);

        ServiceInstanceRequest request = new ServiceInstanceRequest(serviceId, missingHostId, 80);

        postServiceInstanceUnchecked(mockMvc, request, mapper)
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAppServiceInstanceDetails() throws Exception {
        AppServiceRequest appServiceRequest = newAppService("persisted service", "3.0.1");
        long serviceId = postAppServiceForId(mockMvc, appServiceRequest, mapper);

        DataCenterRequest dcRequest = new DataCenterRequest("us-west-1", "California");
        long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("node-js", "up", "awsos", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        ServiceInstanceRequest request = new ServiceInstanceRequest(serviceId, hostId, 8000);

        String instanceJson = postServiceInstance(mockMvc, request, mapper)
                .andExpect(matchesServiceInstanceRequest(request, null, appServiceRequest, hostRequest, mapper))
                .andReturn().getResponse().getContentAsString();

        ServiceInstanceDetails instance = mapper.readValue(instanceJson, ServiceInstanceDetails.class);

        getAppService(mockMvc, serviceId)
                .andExpect(matchesAppServiceRequest(appServiceRequest, serviceId, mapper))
                .andExpect(jsonPath("$.instancesInfo.totalCount", is(1)))
                .andExpect(jsonPath("$.instancesInfo.topInstances[0].instanceId", is((int) instance.getId())))
                .andExpect(jsonPath("$.instancesInfo.topInstances[0].hostId", is((int) hostId)))
                .andExpect(jsonPath("$.instancesInfo.topInstances[0].hostName", is("node-js")))
                .andExpect(jsonPath("$.instancesInfo.topInstances[0].status", is("running")));

        getServiceInstance(mockMvc, instance.getId(), serviceId)
                .andExpect(matchesServiceInstanceRequest(request, instance.getId(), appServiceRequest, hostRequest, mapper));
    }

    @Test
    void testDeleteAppServiceInstance() throws Exception {
        AppServiceRequest appServiceRequest = newAppService("good service", "3.0.1");
        long serviceId = postAppServiceForId(mockMvc, appServiceRequest, mapper);

        DataCenterRequest dcRequest = new DataCenterRequest("us-west-2", "Oregon");
        long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("bad instance", "up", "awsos", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        ServiceInstanceRequest request80 = new ServiceInstanceRequest(serviceId, hostId, 80);
        ServiceInstanceRequest request8080 = new ServiceInstanceRequest(serviceId, hostId, 8080);

        long instanceId80 = postServiceInstanceForId(mockMvc, request80, mapper);
        long instanceId8080 = postServiceInstanceForId(mockMvc, request8080, mapper);

        getHostNode(mockMvc, hostId)
                .andExpect(jsonPath("$.launchedInstances", is(2)));

        mockMvc.perform(delete("/api/services/{serviceId}/instances/{id}", serviceId, instanceId8080))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/services/{serviceId}/instances/{id}", serviceId, instanceId8080))
                .andExpect(status().isNotFound());

        getServiceInstance(mockMvc, instanceId80, serviceId)
                .andExpect(matchesServiceInstanceRequest(request80, instanceId80, appServiceRequest, hostRequest, mapper));

        getHostNode(mockMvc, hostId)
                .andExpect(jsonPath("$.launchedInstances", is(1)));
    }

    @Test
    void testNotAssigningTwoSamePortsOnGivenHost() throws Exception {
        AppServiceRequest serviceRequest1 = newAppService("first service", "1");
        long serviceId1 = postAppServiceForId(mockMvc, serviceRequest1, mapper);

        AppServiceRequest serviceRequest2 = newAppService("second service", "2");
        long serviceId2 = postAppServiceForId(mockMvc, serviceRequest2, mapper);

        DataCenterRequest dcRequest = new DataCenterRequest("us-west-2", "Oregon");
        long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("bad instance", "up", "awsos", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        ServiceInstanceRequest request1 = new ServiceInstanceRequest(serviceId1, hostId, 80);
        ServiceInstanceRequest request2 = new ServiceInstanceRequest(serviceId2, hostId, 80);

        postServiceInstance(mockMvc, request1, mapper)
                .andExpect(matchesServiceInstanceRequest(request1, null, serviceRequest1, hostRequest, mapper));

        postServiceInstanceUnchecked(mockMvc, request2, mapper)
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDeleteNotExistentServiceInstance() throws Exception {
        AppServiceRequest appServiceRequest = newAppService("good service", "3.0.1");
        long serviceId = postAppServiceForId(mockMvc, appServiceRequest, mapper);

        final long instanceId = 10;

        mockMvc.perform(delete("/api/services/{serviceId}/instances/{id}", serviceId, instanceId))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPostSimpleAppService() throws Exception {
        AppServiceRequest appServiceRequest = newAppService("app service 1", "1.0.0_release");

        String responseJson = postAppService(mockMvc, appServiceRequest, mapper)
                .andReturn().getResponse().getContentAsString();

        AppServiceDetails appService = mapper.readValue(responseJson, AppServiceDetails.class);

        getAppService(mockMvc, appService.getId())
                .andExpect(matchesAppServiceRequest(appServiceRequest, appService.getId(), mapper));
    }

    @Test
    void testPostCreateSimpleAppServiceWithId() throws Exception {
        AppServiceRequest request = newAppService("service with id", "4.0");
        String poeToken = UUID.randomUUID().toString();
        long serviceId = 5;

        mockMvc.perform(
                post("/api/services/{id}", serviceId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .header("POE-Token", poeToken)
        )
                .andExpect(status().isNotFound());
    }

    @Test
    void testPostCreateSingleAppServiceWithPostOnce() throws Exception {
        AppServiceRequest request = newAppService("only single service", "1.0");
        String poeToken = UUID.randomUUID().toString();
        Long serviceId = null;

        String requestJson = mapper.writeValueAsString(request);

        for (int i = 0; i < 4; ++i) {
            ResultActions resultActions = mockMvc.perform(
                    post("/api/services")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson)
                            .header("POE-Token", poeToken)
            );

            if (i == 0) {
                resultActions.andExpect(status().isOk());
                serviceId = readAppService(resultActions, mapper).getId();
            }
            else
                resultActions.andExpect(status().isMethodNotAllowed());
        }

        assertNotNull(serviceId);

        getAppService(mockMvc, serviceId)
                .andExpect(matchesAppServiceRequest(request, serviceId, mapper));
    }

    @Test
    void testPostUpdateSimpleAppService() throws Exception {
        AppServiceRequest createRequest = newAppService("super service", "1");

        String responseJson = postAppService(mockMvc, createRequest, mapper)
                .andReturn().getResponse().getContentAsString();

        AppServiceDetails appService = mapper.readValue(responseJson, AppServiceDetails.class);
        AppServiceRequest updateRequest = appServiceUpdate("normal service", "0.1", appService.getDataVersionToken());

        postAppService(mockMvc, updateRequest, appService.getId(), mapper)
                .andExpect(matchesAppServiceRequest(updateRequest, appService.getId(), mapper));

        getAppService(mockMvc, appService.getId())
                .andExpect(matchesAppServiceRequest(updateRequest, appService.getId(), mapper));
    }

    @Test
    void testPutCreateAppService() throws Exception {
        AppServiceRequest createRequest = newAppService("put service", "cs.put");
        final long serviceId = 8;

        putAppService(mockMvc, createRequest, serviceId, mapper)
                .andExpect(matchesAppServiceRequest(createRequest, serviceId, mapper));

        getAppService(mockMvc, serviceId)
                .andExpect(matchesAppServiceRequest(createRequest, serviceId, mapper));
    }

    @Test
    void testPutUpdateAppService() throws Exception {
        AppServiceRequest createRequest = newAppService("cs service", "1.0.0_cs");
        final long serviceId = 10;

        AppServiceDetails appService = readAppService(
                putAppService(mockMvc, createRequest, serviceId, mapper)
                        .andExpect(matchesAppServiceRequest(createRequest, serviceId, mapper)),
                mapper
        );

        AppServiceRequest updateRequest = appServiceUpdate("cs put service", "1.0.1_putcs", appService.getDataVersionToken());
        putAppService(mockMvc, updateRequest, serviceId, mapper)
                .andExpect(matchesAppServiceRequest(updateRequest, serviceId, mapper));

        getAppService(mockMvc, serviceId)
                .andExpect(matchesAppServiceRequest(updateRequest, serviceId, mapper));
    }

    @Test
    void testPatchAppService() throws Exception {
        AppServiceRequest createRequest = newAppService("not-patched service", "2");

        String responseJson = postAppService(mockMvc, createRequest, mapper)
                .andReturn().getResponse().getContentAsString();

        AppServiceDetails appService = mapper.readValue(responseJson, AppServiceDetails.class);
        final String newServiceName = "patched service";
        final long appServiceId = appService.getId();

        Map<String, Object> patchRequest = appServicePatch(
                Optional.of(newServiceName), Optional.empty(), appService.getDataVersionToken()
        );

        AppServiceRequest patchedRequest = newAppService(
                newServiceName, createRequest.getServiceVersion()
        );

        patchAppService(mockMvc, patchRequest, appServiceId, mapper)
                .andExpect(matchesAppServiceRequest(patchedRequest, appServiceId, mapper));

        getAppService(mockMvc, appServiceId)
                .andExpect(matchesAppServiceRequest(patchedRequest, appServiceId, mapper));
    }

    @Test
    void testCreateAndUpdateMultipleTimes() throws Exception {
        AppServiceRequest createRequest = newAppService("updatable app service", "1.0");
        String createJson = postAppService(mockMvc, createRequest, mapper)
                .andReturn().getResponse().getContentAsString();

        AppServiceDetails appService = mapper.readValue(createJson, AppServiceDetails.class);
        final long serviceId = appService.getId();
        String updateToken = appService.getDataVersionToken();

        AppServiceRequest updateRequest1 = appServiceUpdate("still updatable service", "2.0", updateToken);
        String updateJson1 = postAppService(mockMvc, updateRequest1, serviceId, mapper)
                .andReturn().getResponse().getContentAsString();

        updateToken = mapper.readValue(updateJson1, AppServiceDetails.class).getDataVersionToken();

        AppServiceRequest updateRequest2 = appServiceUpdate("still updatable service", "3.0", updateToken);
        String updateJson2 = putAppService(mockMvc, updateRequest2, serviceId, mapper)
                .andReturn().getResponse().getContentAsString();

        updateToken = mapper.readValue(updateJson2, AppServiceDetails.class).getDataVersionToken();

        Map<String, Object> patchRequest = appServicePatch(Optional.of("updatable service once again"), Optional.of("4.0"), updateToken);
        patchAppService(mockMvc, patchRequest, serviceId, mapper);

        AppServiceRequest finalAppService = newAppService("updatable service once again", "4.0");

        getAppService(mockMvc, serviceId)
                .andExpect(matchesAppServiceRequest(finalAppService, serviceId, mapper));
    }

    @Test
    void testUpdateOptimisticLock() throws Exception {
        AppServiceRequest createRequest = newAppService("shared app service", "1.0");
        String createJson = postAppService(mockMvc, createRequest, mapper)
                .andReturn().getResponse().getContentAsString();

        AppServiceDetails appService = mapper.readValue(createJson, AppServiceDetails.class);
        final long serviceId = appService.getId();
        String updateToken = appService.getDataVersionToken();

        AppServiceRequest updateRequest1 = appServiceUpdate("owned app service", "1.1", updateToken);
        AppServiceRequest updateRequest2 = appServiceUpdate("mine app service", "2.0", updateToken);

        putAppService(mockMvc, updateRequest1, serviceId, mapper);

        mockMvc.perform(
                put("/api/services/{id}", serviceId)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateRequest2))
        )
                .andExpect(status().isConflict());
    }

    @Test
    void testDeleteAppService() throws Exception {
        AppServiceRequest createRequest = newAppService("useless service", "1.0.0");

        String responseJson = postAppService(mockMvc, createRequest, mapper)
                .andReturn().getResponse().getContentAsString();

        AppServiceDetails appService = mapper.readValue(responseJson, AppServiceDetails.class);

        mockMvc.perform(delete("/api/services/{id}", appService.getId()))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/services/{id}", appService.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteAppServiceWithInstances() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("us-east-1", "N. Virginia");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("node-1", "up", "debian", dcId);
        final long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        AppServiceRequest serviceRequest = newAppService("test-service", "1.0.0");
        final long serviceId = postAppServiceForId(mockMvc, serviceRequest, mapper);

        ServiceInstanceRequest instanceRequest80 = new ServiceInstanceRequest(serviceId, hostId, 80);
        ServiceInstanceRequest instanceRequest443 = new ServiceInstanceRequest(serviceId, hostId, 443);
        ServiceInstanceRequest instanceRequest8080 = new ServiceInstanceRequest(serviceId, hostId, 8080);

        postServiceInstance(mockMvc, instanceRequest80, mapper);
        postServiceInstance(mockMvc, instanceRequest443, mapper);
        postServiceInstance(mockMvc, instanceRequest8080, mapper);

        mockMvc.perform(delete("/api/services/{id}", serviceId))
                .andExpect(status().isOk());

        getHostNode(mockMvc, hostId)
                .andExpect(jsonPath("$.launchedInstances", is(0)));
    }

    @Test
    void testScalingUpServiceInstances() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("datacenter", "Beyond");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("host1", "up", "mint", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        HostNodeRequest hostRequest2 = new HostNodeRequest("host2", "up", "kali", dcId);
        long hostId2 = postHostNodeForId(mockMvc, hostRequest2, mapper);

        HostNodeRequest hostRequest3 = new HostNodeRequest("host3", "up", "ubuntu", dcId);
        long hostId3 = postHostNodeForId(mockMvc, hostRequest3, mapper);

        AppServiceRequest serviceRequest = newAppService("cassandra", "3.0");
        long serviceId = postAppServiceForId(mockMvc, serviceRequest, mapper);

        ServiceInstanceRequest instanceRequest1 = new ServiceInstanceRequest(serviceId, hostId, 1234);
        long instanceId = postServiceInstanceForId(mockMvc, instanceRequest1, mapper);

        mockMvc.perform(
                post("/api/services/{id}/scale", serviceId)
                        .param("replication", "3")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.startedInstances", is(2)))
                .andExpect(jsonPath("$.removedInstances", is(0)));

        HostNodeDetails host1 = readHostNode(getHostNode(mockMvc, hostId), mapper);
        HostNodeDetails host2 = readHostNode(getHostNode(mockMvc, hostId2), mapper);
        HostNodeDetails host3 = readHostNode(getHostNode(mockMvc, hostId3), mapper);

        int count1 = host1.getLaunchedInstances();
        int count2 = host2.getLaunchedInstances();
        int count3 = host3.getLaunchedInstances();

        assertEquals(
                String.format("Host instances distribution: (%d, %d, %d)", count1, count2, count3),
                3, count1 + count2 + count3
        );
    }

    @Test
    void testUnnecessaryScalingServiceInstances() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("datacenter", "Beyond");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("host1", "up", "mint", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        HostNodeRequest hostRequest2 = new HostNodeRequest("host2", "up", "kali", dcId);
        long hostId2 = postHostNodeForId(mockMvc, hostRequest2, mapper);

        HostNodeRequest hostRequest3 = new HostNodeRequest("host3", "up", "ubuntu", dcId);
        long hostId3 = postHostNodeForId(mockMvc, hostRequest3, mapper);

        AppServiceRequest serviceRequest = newAppService("cassandra", "3.0");
        long serviceId = postAppServiceForId(mockMvc, serviceRequest, mapper);

        ServiceInstanceRequest instanceRequest1 = new ServiceInstanceRequest(serviceId, hostId, 1234);
        long instanceId = postServiceInstanceForId(mockMvc, instanceRequest1, mapper);

        ServiceInstanceRequest instanceRequest2 = new ServiceInstanceRequest(serviceId, hostId2, 1234);
        long instanceId2 = postServiceInstanceForId(mockMvc, instanceRequest2, mapper);

        mockMvc.perform(
                post("/api/services/{id}/scale", serviceId)
                        .param("replication", "2")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.startedInstances", is(0)))
                .andExpect(jsonPath("$.removedInstances", is(0)));

        HostNodeDetails host1 = readHostNode(getHostNode(mockMvc, hostId), mapper);
        HostNodeDetails host2 = readHostNode(getHostNode(mockMvc, hostId2), mapper);
        HostNodeDetails host3 = readHostNode(getHostNode(mockMvc, hostId3), mapper);

        int count1 = host1.getLaunchedInstances();
        int count2 = host2.getLaunchedInstances();
        int count3 = host3.getLaunchedInstances();

        assertEquals("Host 1 should have single instance", 1, count1);
        assertEquals("Host 2 should have single instance", 1, count2);
        assertEquals("Host 3 should have no instances", 0, count3);
    }

    @Test
    void testScalingDownServiceInstances() throws Exception {
        DataCenterRequest dcRequest = new DataCenterRequest("datacenter", "Beyond");
        final long dcId = postDataCenterForId(mockMvc, dcRequest, mapper);

        HostNodeRequest hostRequest = new HostNodeRequest("host1", "up", "mint", dcId);
        long hostId = postHostNodeForId(mockMvc, hostRequest, mapper);

        HostNodeRequest hostRequest2 = new HostNodeRequest("host2", "up", "kali", dcId);
        long hostId2 = postHostNodeForId(mockMvc, hostRequest2, mapper);

        HostNodeRequest hostRequest3 = new HostNodeRequest("host3", "up", "ubuntu", dcId);
        long hostId3 = postHostNodeForId(mockMvc, hostRequest3, mapper);

        AppServiceRequest serviceRequest = newAppService("cassandra", "3.0");
        long serviceId = postAppServiceForId(mockMvc, serviceRequest, mapper);

        ServiceInstanceRequest instanceRequest1 = new ServiceInstanceRequest(serviceId, hostId, 1234);
        ServiceInstanceRequest instanceRequest2 = new ServiceInstanceRequest(serviceId, hostId2, 1234);
        ServiceInstanceRequest instanceRequest3 = new ServiceInstanceRequest(serviceId, hostId3, 12345);
        ServiceInstanceRequest instanceRequest4 = new ServiceInstanceRequest(serviceId, hostId, 4321);
        ServiceInstanceRequest instanceRequest5 = new ServiceInstanceRequest(serviceId, hostId, 9876);

        long instanceId = postServiceInstanceForId(mockMvc, instanceRequest1, mapper);
        long instanceId2 = postServiceInstanceForId(mockMvc, instanceRequest2, mapper);
        long instanceId3 = postServiceInstanceForId(mockMvc, instanceRequest3, mapper);
        long instanceId4 = postServiceInstanceForId(mockMvc, instanceRequest4, mapper);
        long instanceId5 = postServiceInstanceForId(mockMvc, instanceRequest5, mapper);

        mockMvc.perform(
                post("/api/services/{id}/scale", serviceId)
                        .param("replication", "2")
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.startedInstances", is(0)))
                .andExpect(jsonPath("$.removedInstances", is(3)));

        HostNodeDetails host1 = readHostNode(getHostNode(mockMvc, hostId), mapper);
        HostNodeDetails host2 = readHostNode(getHostNode(mockMvc, hostId2), mapper);
        HostNodeDetails host3 = readHostNode(getHostNode(mockMvc, hostId3), mapper);

        int count1 = host1.getLaunchedInstances();
        int count2 = host2.getLaunchedInstances();
        int count3 = host3.getLaunchedInstances();

        assertEquals("Host 1 should have single instance", 1, count1);
        assertEquals("Host 2 should have single instance", 1, count2);
        assertEquals("Host 3 should have no instances", 0, count3);
    }
}
