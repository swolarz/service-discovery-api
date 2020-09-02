package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.HostNodeDetails;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@UtilityClass
class HostNodeTestUtils {

    public Map<String, Object> hostNodePatch(Optional<String> name, Optional<String> status,
                                             Optional<String> operatingSystem, Optional<Long> dataCenterId,
                                             String versionToken) {

        Map<String, Object> updateDictionary = new HashMap<>();

        name.ifPresent(n -> updateDictionary.put("name", n));
        status.ifPresent(s -> updateDictionary.put("status", s));
        operatingSystem.ifPresent(os -> updateDictionary.put("operatingSystem", os));
        dataCenterId.ifPresent(dcId -> updateDictionary.put("dataCenterId", dcId));

        updateDictionary.put("dataVersionToken", versionToken);

        return updateDictionary;
    }

    public ResultActions getHostNodes(MockMvc mockMvc, int page, int perPage) throws Exception {
        return mockMvc.perform(
                get("/api/hosts")
                        .param("page", Integer.toString(page))
                        .param("perPage", Integer.toString(perPage))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().is2xxSuccessful());
    }

    public ResultActions getHostNodeUnchecked(MockMvc mockMvc, long id) throws Exception {
        return mockMvc.perform(
                get("/api/hosts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
        );
    }

    public ResultActions getHostNode(MockMvc mockMvc, long id) throws Exception {
        return getHostNodeUnchecked(mockMvc, id)
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions postHostNode(MockMvc mockMvc, HostNodeRequest request, ObjectMapper mapper) throws Exception {
        String poeToken = UUID.randomUUID().toString();

        return mockMvc.perform(
                post("/api/hosts")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .header("POE-Token", poeToken)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public long postHostNodeForId(MockMvc mockMvc, HostNodeRequest request, ObjectMapper mapper) throws Exception {
        String jsonResponse = postHostNode(mockMvc, request, mapper)
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(jsonResponse, HostNodeDetails.class).getId();
    }

    public ResultActions postHostNode(MockMvc mockMvc, HostNodeRequest request, long id, ObjectMapper mapper) throws Exception {
        return mockMvc.perform(
                post("/api/hosts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions putHostNode(MockMvc mockMvc, HostNodeRequest request, long id, ObjectMapper mapper) throws Exception {
        return mockMvc.perform(
                put("/api/hosts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions patchHostNode(MockMvc mockMvc, Map<String, Object> patch, long id, ObjectMapper mapper) throws Exception {
        return mockMvc.perform(
                patch("/api/hosts/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public HostNodeDetails readHostNode(ResultActions resultActions, ObjectMapper mapper) throws Exception {
        String hostJson = resultActions.andReturn().getResponse().getContentAsString();
        return mapper.readValue(hostJson, HostNodeDetails.class);
    }

    public ResultMatcher matchesHostNodeRequest(HostNodeRequest request, Long id,
                                                  DataCenterRequest dcRequest, int expectedInstances,
                                                  ObjectMapper mapper) {
        return result -> {
            String response = result.getResponse().getContentAsString();
            HostNodeDetails hostNodeDetails = mapper.readValue(response, HostNodeDetails.class);

            assertHostNodeRequestMatches(request, dcRequest, expectedInstances, hostNodeDetails, true);

            if (id != null)
                assertEquals(id.longValue(), hostNodeDetails.getId());
        };
    }

    public void assertHostNodeRequestMatches(HostNodeRequest request, DataCenterRequest dataCenterRequest, int expectedInstances,
                                             HostNodeDetails hostNodeDetails, boolean updatable) {

        assertEquals(request.getName(), hostNodeDetails.getName());
        assertEquals(request.getStatus(), hostNodeDetails.getStatus());
        assertEquals(request.getOperatingSystem(), hostNodeDetails.getOperatingSystem());
        assertEquals(expectedInstances, hostNodeDetails.getLaunchedInstances());

        assertEquals(request.getDataCenterId(), hostNodeDetails.getDataCenterId());
        assertEquals(dataCenterRequest.getName(), hostNodeDetails.getDataCenterName());

        if (request.getStatus().toLowerCase().equals("status"))
            assertNotNull(hostNodeDetails.getLaunchedAt());

        if (updatable)
            assertNotNull(hostNodeDetails.getDataVersionToken());
    }
}
