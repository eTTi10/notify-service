package com.lguplus.fleta.api.inner.httppush;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.mapper.HttpPushMultiMapper;
import com.lguplus.fleta.data.mapper.HttpPushSingleMapper;
import com.lguplus.fleta.service.httppush.HttpPushService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {HttpPushController.class
        , ArgumentResolverConfig.class
        , MessageConverterConfig.class})
class HttpPushControllerTest {

    private static final String URL_TEMPLATE_SINGLE = "/notify/httppush/single";
    private static final String URL_TEMPLATE_MULTI = "/notify/httppush/multi";
    private static final String SUCCESS_CODE = "200";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HttpPushSingleMapper httpPushSingleMapper;

    @MockBean
    private HttpPushMultiMapper httpPushMultiMapper;

    @MockBean
    private HttpPushService httpPushService;

    @Test
    @DisplayName("정상적으로 단건푸시가 성공하는지 확인")
    void whenRequestSinglePush_thenReturnSuccess() throws Exception {
        // given
        HttpPushResponseDto httpPushResponseDto = HttpPushResponseDto.builder().build();

        given(httpPushService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("app_id", "lguplushdtvgcm");
        paramMap.put("service_id", "30011");
        paramMap.put("push_type", "G");
        paramMap.put("users", List.of("01099991234"));
        paramMap.put("msg", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"");

        String content = objectMapper.writeValueAsString(paramMap);

        // when
        MvcResult mvcResult = mockMvc.perform(post(URL_TEMPLATE_SINGLE).content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        // then
        assertThat(response).contains(SUCCESS_CODE);    // 성공 코드가 있는지 확인
    }

    @Test
    @DisplayName("정상적으로 멀티푸시가 성공하는지 확인")
    void whenRequestMultiPush_thenReturnSuccess() throws Exception {
        // given
        HttpPushResponseDto httpPushResponseDto = HttpPushResponseDto.builder().build();

        given(httpPushService.requestHttpPushMulti(any())).willReturn(httpPushResponseDto);

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("app_id", "lguplushdtvgcm");
        paramMap.put("service_id", "30011");
        paramMap.put("push_type", "G");
        paramMap.put("users", List.of("01099991234", "MTIzDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI="));
        paramMap.put("msg", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}\"");

        String content = objectMapper.writeValueAsString(paramMap);

        // when
        MvcResult mvcResult = mockMvc.perform(post(URL_TEMPLATE_MULTI).content(content).contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        // then
        assertThat(response).contains(SUCCESS_CODE);    // 성공 코드가 있는지 확인
    }

}