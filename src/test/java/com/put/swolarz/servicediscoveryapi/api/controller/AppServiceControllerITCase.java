package com.put.swolarz.servicediscoveryapi.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.put.swolarz.servicediscoveryapi.domain.discovery.dto.AppServiceDetails;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static com.put.swolarz.servicediscoveryapi.api.controller.AppServiceTestUtils.*;


@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AppServiceControllerITCase {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper mapper;


    @BeforeEach
    void setUp() {
        this.mapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void testLoadingCreatedServices() {
        AppServiceRequest request1 = newAppService("svc1", "1.0");
        AppServiceRequest request2 = newAppService("servajs", "2.0");
        AppServiceRequest request3 = newAppService("niemogepodniesctegoservajsu", "4.0");
        AppServiceRequest request4 = newAppService("mowisieserwis", "5.1");
    }

    @Test
    void testGetAppService() {
    }

    @Test
    void testGetAppServiceInstances() {
    }

    @Test
    void testGetAppServiceInstanceDetails() {
    }

    @Test
    void testPostAppServiceInstance() {
    }

    @Test
    void testDeleteAppServiceInstance() {
    }

    @Test
    void testPostSimpleAppService() throws Exception {
        AppServiceRequest appServiceRequest = newAppService("app service 1", "1.0.0_release");

        postAppService(mockMvc, appServiceRequest, mapper)
                .andExpect(matchesAppServiceRequest(appServiceRequest, null, mapper));
    }

    @Test
    void testPostCreateSimpleAppServiceWithId() throws Exception {
        AppServiceRequest request = newAppService("service with id", "4.0");
        long serviceId = 5;

        postAppService(mockMvc, request, serviceId, mapper)
                .andExpect(matchesAppServiceRequest(request, serviceId, mapper));
    }

    @Test
    void testPostUpdateSimpleAppService() throws Exception {
        AppServiceRequest createRequest = newAppService("super service", "1");

        String responseJson = postAppService(mockMvc, createRequest, mapper)
                .andExpect(matchesAppServiceRequest(createRequest, null, mapper))
                .andReturn().getResponse().getContentAsString();

        AppServiceDetails appService = mapper.readValue(responseJson, AppServiceDetails.class);
        AppServiceRequest updateRequest = newAppService("normal service", "0.1");

        postAppService(mockMvc, updateRequest, appService.getId(), mapper)
                .andExpect(matchesAppServiceRequest(createRequest, appService.getId(), mapper));
    }

    @Test
    void testPutCreateAppService() throws Exception {
        AppServiceRequest createRequest = newAppService("put service", "cs.put");
        final long serviceId = 8;

        putAppService(mockMvc, createRequest, serviceId, mapper)
                .andExpect(matchesAppServiceRequest(createRequest, serviceId, mapper));
    }

    @Test
    void testPutUpdateAppService() throws Exception {
        AppServiceRequest createRequest = newAppService("cs service", "1.0.0_cs");
        final long serviceId = 10;

        putAppService(mockMvc, createRequest, serviceId, mapper)
                .andExpect(matchesAppServiceRequest(createRequest, serviceId, mapper));

        AppServiceRequest updateRequest = newAppService("cs put service", "1.0.1_putcs");

        putAppService(mockMvc, updateRequest, serviceId, mapper)
                .andExpect(matchesAppServiceRequest(updateRequest, serviceId, mapper));
    }

    @Test
    void testPatchAppService() throws Exception {
        AppServiceRequest createRequest = newAppService("not-patched service", "2");

        String responseJson = postAppService(mockMvc, createRequest, mapper)
                .andExpect(matchesAppServiceRequest(createRequest, null, mapper))
                .andReturn().getResponse().getContentAsString();

        AppServiceDetails appService = mapper.readValue(responseJson, AppServiceDetails.class);
    }

    @Test
    void testDeleteAppService() {
    }
}