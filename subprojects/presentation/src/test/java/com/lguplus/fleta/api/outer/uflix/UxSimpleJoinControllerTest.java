package com.lguplus.fleta.api.outer.uflix;

import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.response.inner.SmsGatewayResponseDto;
import com.lguplus.fleta.data.mapper.UxSimpleJoinSmsMapper;
import com.lguplus.fleta.service.uflix.UxSimpleJoinService;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {UxSimpleJoinController.class
    , ArgumentResolverConfig.class
    , MessageConverterConfig.class})
class UxSimpleJoinControllerTest {

    private static final String URL_TEMPLATE = "/smartux/gw/UXSimpleJoin.php";
    private static final String SUCCESS_CODE = "0000";
    private static final String CTN_EMPTY_CODE = "5000";
    private static final String CTN_WRONG_CODE = "5001";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UxSimpleJoinSmsMapper uxSimpleJoinSmsMapper;

    @MockBean
    private UxSimpleJoinService uxSimpleJoinService;

    @Test
    @DisplayName("??????????????? tvG ???????????? ?????? ?????? ?????? SMS ????????? ??????????????? ??????")
    void whenRequestUxSimpleJoinSms_thenReturnSuccess() throws Exception {
        // given
        SmsGatewayResponseDto smsGatewayResponseDto = SmsGatewayResponseDto.builder().flag("0000").build();

        given(uxSimpleJoinService.requestUxSimpleJoinSms(any())).willReturn(smsGatewayResponseDto);

        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("sa_id", "500058151453");
        paramMap.add("stb_mac", "001c.627e.039c");
        paramMap.add("ctn", "01055805424");

        // when
        MvcResult mvcResult = mockMvc.perform(get(URL_TEMPLATE).accept(MediaType.APPLICATION_JSON).params(paramMap))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        // then
        assertThat(response).contains(SUCCESS_CODE);    // ?????? ????????? ????????? ??????
    }

    @Test
    @DisplayName("???????????? ?????? ????????????")
    void whenRequestUxSimpleJoinSms_withEmptyCtn_thenReturnFailure() throws Exception {
        // given
        SmsGatewayResponseDto smsGatewayResponseDto = SmsGatewayResponseDto.builder().flag("9999").build();

        given(uxSimpleJoinService.requestUxSimpleJoinSms(any())).willReturn(smsGatewayResponseDto);

        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("sa_id", "500058151453");
        paramMap.add("stb_mac", "001c.627e.039c");
        paramMap.add("ctn", null);

        // when
        MvcResult mvcResult = mockMvc.perform(get(URL_TEMPLATE).accept(MediaType.APPLICATION_JSON).params(paramMap))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        // then
        assertThat(response).contains(CTN_EMPTY_CODE);    // 5000 ????????? ????????? ??????
    }

    @Test
    @DisplayName("????????? ???????????? ????????????")
    void whenRequestUxSimpleJoinSms_withWrongCtn_thenReturnFailure() throws Exception {
        // given
        SmsGatewayResponseDto smsGatewayResponseDto = SmsGatewayResponseDto.builder().flag("9999").build();

        given(uxSimpleJoinService.requestUxSimpleJoinSms(any())).willReturn(smsGatewayResponseDto);

        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("sa_id", "500058151453");
        paramMap.add("stb_mac", "001c.627e.039c");
        paramMap.add("ctn", "??????");

        // when
        MvcResult mvcResult = mockMvc.perform(get(URL_TEMPLATE).accept(MediaType.APPLICATION_JSON).params(paramMap))
            .andExpect(status().isOk())
            .andDo(print())
            .andReturn();

        String response = mvcResult.getResponse().getContentAsString();

        // then
        assertThat(response).contains(CTN_WRONG_CODE);    // 5001 ????????? ????????? ??????
    }

}