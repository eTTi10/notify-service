package com.lguplus.fleta.api.inner.httppush;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.response.inner.HttpPushResponseDto;
import com.lguplus.fleta.data.mapper.HttpPushSingleMapper;
import com.lguplus.fleta.service.httppush.HttpSinglePushService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {HttpSinglePushController.class
    , ArgumentResolverConfig.class
    , MessageConverterConfig.class})
class HttpSinglePushControllerTest {

    private static final String URL_TEMPLATE_SINGLE = "/notify/httppush/single";
    private static final String SUCCESS_CODE = "200";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HttpPushSingleMapper httpPushSingleMapper;

    @MockBean
    private HttpSinglePushService httpSinglePushService;

    @Test
    @DisplayName("정상적으로 단건푸시가 성공하는지 확인")
    void whenRequestSinglePush_thenReturnSuccess() throws Exception {
        // given
        HttpPushResponseDto httpPushResponseDto = HttpPushResponseDto.builder().build();

        given(httpSinglePushService.requestHttpPushSingle(any())).willReturn(httpPushResponseDto);

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
    @DisplayName("단말 정보 조회 및 푸시 성공 정상 케이스")
    void testGetDeviceInfosAndPushRequest() throws Exception {
        // given
        HttpPushResponseDto httpPushResponseDto = HttpPushResponseDto.builder().build();

        given(httpSinglePushService.getDeviceInfosAndPushRequest(any())).willReturn(httpPushResponseDto);

        Map<String, Object> queryParams = new HashMap<>();

        Map<String, String> paramsList = new HashMap<>();
        paramsList.put("service_push_status", "\"Y\"");

        queryParams.put("service_type", "H");
        queryParams.put("sa_id", "M14080700169");
        queryParams.put("send_code", "termsAgree");
        queryParams.put("items", List.of("\"badge!^1\""));
        queryParams.put("reserve", paramsList);

        String requestContent = objectMapper.writeValueAsString(queryParams);

        MvcResult mvcResult = mockMvc.perform(post("/notify/httppush/getdeviceinfosAndsingle")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .content(requestContent)
                ).andExpect(status().isOk())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        // then
        assertThat(response).contains(SUCCESS_CODE);    // 성공 코드가 있는지 확인
    }

}