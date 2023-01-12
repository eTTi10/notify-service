package com.lguplus.fleta.api.outer.latest;

import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.service.latest.MobileLatestService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {MobileLatestController.class
    , ArgumentResolverConfig.class})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MobileLatestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MobileLatestService service;

    private static final String V1_URL = "/mobile/hdtv/v1/latest";

    private static final String COMM_URL = "/mobile/hdtv/comm/latest";

    @Test
    @Order(1)
    @DisplayName("1. 최신회 알림 등록 v1 정상 파라미터")
    void insertLatest_validParam_v1() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "500058151453");
        queryParams.add("stb_mac", "001c.627e.039c");
        queryParams.add("ctn", "01012345678");
        queryParams.add("cat_id", "T3021");
        queryParams.add("cat_name", "놀라운 대회 스타킹");
        queryParams.add("reg_id", "500058151453");

        MvcResult mvcResult = mvc.perform(post(V1_URL)
                .accept(APPLICATION_JSON)
                .queryParams(queryParams))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();

        assertEquals(200, status);
    }

    @Test
    @Order(2)
    @DisplayName("2. 최신회 알림 등록 v1 비정상 파라미터")
    void insertLatest_invalidParam_v1() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "500058151453");
        queryParams.add("stb_mac", "001c.627e.039c");

        MvcResult mvcResult = mvc.perform(post(V1_URL)
                .accept(APPLICATION_JSON)
                .queryParams(queryParams))
            .andExpect(status().isBadRequest())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();

        assertEquals(400, status);
    }

    @Test
    @Order(3)
    @DisplayName("3. 최신회 알림 등록 comm 정상 파라미터")
    void insertLatest_validParam_comm() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "500058151453");
        queryParams.add("stb_mac", "001c.627e.039c");
        queryParams.add("ctn", "01012345678");
        queryParams.add("cat_id", "T3021");
        queryParams.add("cat_name", "놀라운 대회 스타킹");
        queryParams.add("reg_id", "500058151453");

        MvcResult mvcResult = mvc.perform(post(COMM_URL)
                .accept(APPLICATION_JSON)
                .queryParams(queryParams))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();

        assertEquals(200, status);
    }

    @Test
    @Order(4)
    @DisplayName("4. 최신회 알림 등록 comm 비정상 파라미터")
    void insertLatest_invalidParam_comm() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "500058151453");
        queryParams.add("stb_mac", "001c.627e.039c");

        MvcResult mvcResult = mvc.perform(post(COMM_URL)
                .accept(APPLICATION_JSON)
                .queryParams(queryParams))
            .andExpect(status().isBadRequest())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();

        assertEquals(400, status);
    }

    @Test
    @Order(5)
    @DisplayName("5. 최신회 알림 삭제 v1 정상 파라미터")
    void deleteLatest_validParam_v1() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "500058151453");
        queryParams.add("stb_mac", "001c.627e.039c");
        queryParams.add("ctn", "01012345678");
        queryParams.add("cat_id", "T3021");

        MvcResult mvcResult = mvc.perform(delete(COMM_URL)
                .accept(APPLICATION_JSON)
                .queryParams(queryParams))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();

        assertEquals(200, status);
    }

    @Test
    @Order(6)
    @DisplayName("6. 최신회 알림 삭제 v1 비정상 파라미터")
    void deleteLatest_invalidParam_v1() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "500058151453");

        MvcResult mvcResult = mvc.perform(delete(COMM_URL)
                .accept(APPLICATION_JSON)
                .queryParams(queryParams))
            .andExpect(status().isBadRequest())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();

        assertEquals(400, status);
    }

    @Test
    @Order(7)
    @DisplayName("7. 최신회 알림 삭제 comm 정상 파라미터")
    void deleteLatest_validParam_comm() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "500058151453");
        queryParams.add("stb_mac", "001c.627e.039c");
        queryParams.add("ctn", "01012345678");
        queryParams.add("cat_id", "T3021");

        MvcResult mvcResult = mvc.perform(delete(COMM_URL)
                .accept(APPLICATION_JSON)
                .queryParams(queryParams))
            .andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();

        assertEquals(200, status);
    }

    @Test
    @Order(8)
    @DisplayName("8. 최신회 알림 삭제 comm 비정상 파라미터")
    void deleteLatest_invalidParam_comm() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "500058151453");

        MvcResult mvcResult = mvc.perform(delete(COMM_URL)
                .accept(APPLICATION_JSON)
                .queryParams(queryParams))
            .andExpect(status().isBadRequest())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();

        assertEquals(400, status);
    }
}