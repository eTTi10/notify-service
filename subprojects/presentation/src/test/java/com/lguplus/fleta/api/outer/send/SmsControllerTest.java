package com.lguplus.fleta.api.outer.send;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.data.mapper.SendSmsCodeMapper;
import com.lguplus.fleta.service.send.SmsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {SmsController.class
                        , ArgumentResolverConfig.class
                        , MessageConverterConfig.class})
class SmsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SmsService smsService;

    @MockBean
    private SendSmsCodeMapper sendSmsCodeMapper;

    private final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setUp() {

        // Mock Dto
        SmsGatewayResponseDto smsGatewayResponseDto = SmsGatewayResponseDto.builder()
                .flag("0000")
                .message("성공")
                .build();

        // Mock Method
        given(smsService.sendSmsCode(any())).willReturn(smsGatewayResponseDto);

        //
    }

    @Test
    void sendSmsCode() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "M15030600001");
        queryParams.add("stb_mac", "v150.3060.0001");
        queryParams.add("sms_cd", "S001");
        queryParams.add("ctn", "01051603997");
        queryParams.add("replacement", "http://google.com/start/we09gn2ks");

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/mims/sendSms")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding("UTF-8")
                                .queryParams(queryParams)
                ).andExpect(status().isOk())
                .andReturn();


        MockHttpServletResponse mockHttpServletResponse = mvcResult.getResponse();
        int status = mockHttpServletResponse.getStatus();
        String responseString = mockHttpServletResponse.getContentAsString();

        Assertions.assertThat(responseString.contains("0000")); // 성공 플래그가 있는지 확인

    }
}