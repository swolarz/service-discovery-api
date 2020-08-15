package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.AppServiceDetails;
import lombok.NonNull;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@UtilityClass
class AppServiceTestUtils {

    public AppServiceRequest newAppService(String name, String serviceVersion) {
        return AppServiceRequest.builder()
                .name(name)
                .serviceVersion(serviceVersion)
                .build();
    }

    public AppServiceRequest appServiceUpdate(String name, String serviceVersion, String versionToken) {
        return AppServiceRequest.builder()
                .name(name)
                .serviceVersion(serviceVersion)
                .dataVersionToken(versionToken)
                .build();
    }

    public Map<String, Object> appServicePatch(Optional<String> name, Optional<String> serviceVersion, @NonNull String versionToken) {
        Map<String, Object> updateDictionary = new HashMap<>();

        name.ifPresent(n -> updateDictionary.put("name", n));
        serviceVersion.ifPresent(sv -> updateDictionary.put("serviceVersion", sv));

        updateDictionary.put("dataVersionToken", versionToken);

        return updateDictionary;
    }

    public ResultActions postAppService(MockMvc mockMvc, AppServiceRequest request, ObjectMapper mapper) throws Exception {
        String poeToken = UUID.randomUUID().toString();

        return mockMvc.perform(
                post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .header("POE-Token", poeToken)
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions postAppService(MockMvc mockMvc, AppServiceRequest request, long id, ObjectMapper mapper) throws Exception {
        return mockMvc.perform(
                post("/api/services/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public ResultActions putAppService(MockMvc mockMvc, AppServiceRequest request, long id, ObjectMapper mapper) throws Exception {
        return mockMvc.perform(
                put("/api/services/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
        )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    public ResultMatcher matchesAppServiceRequest(AppServiceRequest appServiceRequest, Long id, ObjectMapper mapper) {
        return result -> {
            String response = result.getResponse().getContentAsString();
            AppServiceDetails appService = mapper.readValue(response, AppServiceDetails.class);

            assertAppServiceRequestMatches(appServiceRequest, appService);

            if (id != null)
                assertEquals(id.longValue(), appService.getId());
        };
    }

    private void assertAppServiceRequestMatches(AppServiceRequest request, AppServiceDetails response) {
        assertEquals(request.getName(), response.getName());
        assertEquals(request.getServiceVersion(), response.getServiceVersion());
        assertNotNull(request.getDataVersionToken());
    }
}
