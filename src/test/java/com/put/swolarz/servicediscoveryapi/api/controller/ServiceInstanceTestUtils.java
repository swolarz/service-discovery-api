package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.ServiceInstanceDetails;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@UtilityClass
class ServiceInstanceTestUtils {

    public ResultActions getServiceInstances(MockMvc mockMvc, long appServiceId, int page, int perPage) throws Exception {
        return mockMvc.perform(
                get("/api/services/{id}/instances", appServiceId)
                        .param("page", Integer.toString(page))
                        .param("perPage", Integer.toString(perPage))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is2xxSuccessful());
    }

    public ResultActions getServiceInstanceUnchecked(MockMvc mockMvc, long id, long appServiceId) throws Exception {
        return mockMvc.perform(
                get("/api/services/{serviceId}/instances/{id}", appServiceId, id)
                        .accept(MediaType.APPLICATION_JSON)
        );
    }

    public ResultActions getServiceInstance(MockMvc mockMvc, long id, long appServiceId) throws Exception {
        return getServiceInstanceUnchecked(mockMvc, id, appServiceId)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions postServiceInstanceUnchecked(MockMvc mockMvc, ServiceInstanceRequest request, ObjectMapper mapper)
            throws Exception {

        String poeToken = UUID.randomUUID().toString();

        return mockMvc.perform(
                post("/api/services/{serviceId}/instances", request.getAppServiceId())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .header("POE-Token", poeToken)
        );
    }

    public ResultActions postServiceInstance(MockMvc mockMvc, ServiceInstanceRequest request, ObjectMapper mapper)
            throws Exception {

        return postServiceInstanceUnchecked(mockMvc, request, mapper)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public long postServiceInstanceForId(MockMvc mockMvc, ServiceInstanceRequest request, ObjectMapper mapper)
            throws Exception {

        String jsonResponse = postServiceInstance(mockMvc, request, mapper)
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(jsonResponse, ServiceInstanceDetails.class).getId();
    }

    public ResultMatcher matchesServiceInstanceRequest(ServiceInstanceRequest request, Long id, AppServiceRequest serviceRequest,
                                                       HostNodeRequest hostRequest, ObjectMapper mapper) {
        return result -> {
            String response = result.getResponse().getContentAsString();
            ServiceInstanceDetails serviceInstanceDetails = mapper.readValue(response, ServiceInstanceDetails.class);

            assertDataCenterRequestMatches(serviceInstanceDetails, request, serviceRequest, hostRequest);

            if (id != null)
                assertEquals(id.longValue(), serviceInstanceDetails.getId());
        };
    }

    public void assertDataCenterRequestMatches(ServiceInstanceDetails instanceDetails, ServiceInstanceRequest instanceRequest,
                                               AppServiceRequest serviceRequest, HostNodeRequest hostRequest) {

        assertEquals(instanceRequest.getAppServiceId(), instanceDetails.getAppServiceId());
        assertEquals(serviceRequest.getName(), instanceDetails.getAppServiceName());

        assertEquals(instanceRequest.getHostNodeId(), instanceDetails.getHostNodeId());
        assertEquals(hostRequest.getName(), instanceDetails.getHostNodeName());

        assertEquals(instanceRequest.getPort(), instanceDetails.getPort());

        assertEquals("up", instanceDetails.getStatus().toLowerCase());
        assertNotNull(instanceDetails.getStartedAt());
    }
}
