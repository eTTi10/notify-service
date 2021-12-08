package com.lguplus.fleta.api.inner.push;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.service.push.PushAnnouncementService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {PushAnnounceController.class
        , ArgumentResolverConfig.class
        , MessageConverterConfig.class})
@Slf4j
class PushAnnounceControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PushAnnouncementService pushAnnouncementService;

    private final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            ;
            //.registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        // Mock Dto
        PushClientResponseDto dto = PushClientResponseDto.builder().build();

        // Mock Method
        given(pushAnnouncementService.requestAnnouncement(any())).willReturn(dto);
    }

    @Test
    void pushRequestAnnouncement() throws Exception {

        List<String> list = new ArrayList<>();
        list.add("badge!^1");
        list.add("sound!^ring.caf");
        list.add("cm!^aaaa");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("app_id", "lguplushdtvgcm");
        queryParams.put("push_type", "G");
        queryParams.put("service_id", "30011");
        queryParams.put("msg", "\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"");
        queryParams.put("items",list);

        String requestContent = MAPPER.writeValueAsString(queryParams);

        log.debug("===: " + requestContent);

        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.post("/smartux/v1/announcement")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(requestContent)
                ).andExpect(status().isOk())
                .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        //System.out.println("TEST >> ["+responseString+"]");
        log.debug("TEST >> ["+responseString+"]");
        Assertions.assertThat(status).isEqualTo(200);
    }

}