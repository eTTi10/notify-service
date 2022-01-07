package com.lguplus.fleta.api.outer.send;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.response.SuccessResponseDto;
import com.lguplus.fleta.data.mapper.SendMmsRequestMapper;
import com.lguplus.fleta.service.send.MmsService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {MmsController.class
        , ArgumentResolverConfig.class
        , MessageConverterConfig.class})
class MmsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MmsService mmsService;

    @MockBean
    private SendMmsRequestMapper sendMmsRequestMapper;

    private final ObjectMapper MAPPER = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @BeforeEach
    void setUp() {


        // Mock Dto
        SuccessResponseDto successResponseDto = SuccessResponseDto.builder().build();

        // Mock Method
        given(mmsService.sendMms(any())).willReturn(successResponseDto);

        //
    }

    @Test
    void sendSmsCode() throws Exception {

        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("sa_id", "M15030600001");
        queryParams.add("stb_mac", "v150.3060.0001");
        queryParams.add("mms_cd", "M011");
        queryParams.add("ctn", "01051603997");
        queryParams.add("replacement", "영희|컴퓨터");

        MvcResult mvcResult = mockMvc.perform(
                        MockMvcRequestBuilders.post("/mims/sendMms")
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