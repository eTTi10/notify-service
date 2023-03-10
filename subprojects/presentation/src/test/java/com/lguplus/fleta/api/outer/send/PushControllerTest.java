package com.lguplus.fleta.api.outer.send;

import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.response.PushServiceResultDto;
import com.lguplus.fleta.data.dto.response.SendPushResponseDto;
import com.lguplus.fleta.service.send.PushService;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {PushController.class
    , ArgumentResolverConfig.class
    , MessageConverterConfig.class})
class PushControllerTest {

    private static final String URL_TEMPLATE = "/mims/sendPushCode";
    private static final String SUCCESS_MESSAGE = "성공";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PushService pushService;

    @Test
    @DisplayName(value = "CODE를 이용한 PUSH요청이 성공을 리턴하는지 확인하는 테스트")
    void sendPushCode() throws Exception {

        //given
        SendPushResponseDto sendPushResponseDto = SendPushResponseDto.builder()
            .flag("0000")
            .message("성공")
            .service(List.of(PushServiceResultDto.builder().flag("0000").message("성공").type("C").build()))
            .build();

        given(pushService.sendPushCode(any())).willReturn(sendPushResponseDto);

        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("sa_id", "");
        paramMap.put("stb_mac", "");
        paramMap.put("reg_id", "");
        paramMap.put("send_code", "");
        paramMap.put("push_type", "");
        paramMap.put("reg_type", "");
        paramMap.put("service_type", "");

        String body = "<request>\n" +
            "    <reserve>\n" +
            "        <address>111111</address>\n" +
            "        <unumber>948-0719</unumber>\n" +
            "        <req_date>202002141124</req_date>\n" +
            "    </reserve>\n" +
            "    <items>\n" +
            "        <item>badge!^1</item>\n" +
            "        <item>sound!^ring.caf</item>\n" +
            "        <item>cm!^aaaa</item>\n" +
            "    </items>\n" +
            "</request>";

        //when

        MvcResult mvcResult = mockMvc.perform(post(URL_TEMPLATE)
                .queryParam("sa_id", "500058151453")
                .queryParam("stb_mac", "001c.627e.039c")
                .queryParam("reg_id", "M00020200205")
                .queryParam("send_code", "P001")
                .queryParam("push_type", "A")
                .queryParam("reg_type", "1")
                .queryParam("service_type", "TV")
                .content(body)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        MockHttpServletResponse mockHttpServletResponse = mvcResult.getResponse();
        String responseString = mockHttpServletResponse.getContentAsString(StandardCharsets.UTF_8);

        //then
        Assertions.assertThat(responseString).contains(SUCCESS_MESSAGE); // 성공 플래그가 있는지 확인

    }

    @Test
    @DisplayName(value = "body를 입력하지 않을 경우 Exception 테스트")
    void sendPushCode_Exception() throws Exception {

        //given
        Map<String, String> paramMap = new HashMap<>();

        paramMap.put("sa_id", "");
        paramMap.put("stb_mac", "");
        paramMap.put("reg_id", "");
        paramMap.put("send_code", "");
        paramMap.put("push_type", "");
        paramMap.put("reg_type", "");
        paramMap.put("service_type", "");

        String body = "";

        //when

        MvcResult mvcResult = mockMvc.perform(post(URL_TEMPLATE)
                .queryParam("sa_id", "500058151453")
                .queryParam("stb_mac", "001c.627e.039c")
                .queryParam("reg_id", "M00020200205")
                .queryParam("send_code", "P001")
                .queryParam("push_type", "A")
                .queryParam("reg_type", "1")
                .queryParam("service_type", "TV")
                .content(body)
                .contentType(MediaType.APPLICATION_XML)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(print())
            .andReturn();

    }

}