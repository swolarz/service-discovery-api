package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.DataCenterDetails;
import lombok.experimental.UtilityClass;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@UtilityClass
class DataCenterTestUtils {

    public Map<String, Object> dataCenterPatch(Optional<String> name, Optional<String> location, String versionToken) {
        Map<String, Object> updateDictionary = new HashMap<>();

        name.ifPresent(n -> updateDictionary.put("name", n));
        location.ifPresent(l -> updateDictionary.put("location", l));

        updateDictionary.put("dataVersionToken", versionToken);

        return updateDictionary;
    }

    public ResultActions getDataCenters(MockMvc mockMvc, int page, int perPage) throws Exception {
        return mockMvc.perform(
                get("/api/datacenters")
                        .param("page", Integer.toString(page))
                        .param("perPage", Integer.toString(perPage))
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    public ResultActions getDataCenterUnchecked(MockMvc mockMvc, long id) throws Exception {
        return mockMvc.perform(
                get("/api/datacenters/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
        );
    }

    public ResultActions getDataCenter(MockMvc mockMvc, long id) throws Exception {
        return getDataCenterUnchecked(mockMvc, id)
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    public ResultActions postDataCenter(MockMvc mockMvc, DataCenterRequest request, ObjectMapper mapper) throws Exception {
        String poeToken = UUID.randomUUID().toString();

        return mockMvc.perform(
                post("/api/datacenters")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .header("POE-Token", poeToken)
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(matchesDataCenterRequest(request, null, mapper));
    }

    public long postDataCenterForId(MockMvc mockMvc, DataCenterRequest request, ObjectMapper mapper) throws Exception {
        String jsonResponse = postDataCenter(mockMvc, request, mapper)
                .andReturn().getResponse().getContentAsString();

        return mapper.readValue(jsonResponse, DataCenterDetails.class).getId();
    }

    public ResultActions postDataCenter(MockMvc mockMvc, DataCenterRequest request, long id, ObjectMapper mapper) throws Exception {
        return mockMvc.perform(
                post("/api/datacenters/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(matchesDataCenterRequest(request, id, mapper));
    }

    public ResultActions putDataCenter(MockMvc mockMvc, DataCenterRequest request, long id, ObjectMapper mapper) throws Exception {
        return mockMvc.perform(
                put("/api/datacenters/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(matchesDataCenterRequest(request, id, mapper));
    }

    public ResultActions patchDataCenter(MockMvc mockMvc, Map<String, Object> patch, long id, ObjectMapper mapper) throws Exception {
        return mockMvc.perform(
                patch("/api/datacenters/{id}", id)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(patch))
        )
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    public DataCenterDetails readDataCenter(ResultActions resultActions, ObjectMapper mapper) throws Exception {
        String dcJson = resultActions.andReturn().getResponse().getContentAsString();
        return mapper.readValue(dcJson, DataCenterDetails.class);
    }

    public ResultMatcher matchesDataCenterRequest(DataCenterRequest request, Long id, ObjectMapper mapper) {
        return result -> {
            String response = result.getResponse().getContentAsString();
            DataCenterDetails dataCenterDetails = mapper.readValue(response, DataCenterDetails.class);

            assertDataCenterRequestMatches(request, dataCenterDetails, true);

            if (id != null)
                assertEquals(id.longValue(), dataCenterDetails.getId());
        };
    }

    public void assertDataCenterRequestMatches(DataCenterRequest request, DataCenterDetails dataCenterDetails, boolean updatable) {
        assertEquals(request.getName(), dataCenterDetails.getName());
        assertEquals(request.getLocation(), dataCenterDetails.getLocation());

        if (updatable)
            assertNotNull(dataCenterDetails.getDataVersionToken());
    }
}
