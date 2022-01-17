package com.lguplus.fleta.api.outer.uflix;

import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.mapper.UxSimpleJoinSmsMapper;
import com.lguplus.fleta.service.uflix.UxSimpleJoinService;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {UxSimpleJoinController.class
        , ArgumentResolverConfig.class
        , MessageConverterConfig.class})
class UxSimpleJoinControllerTest {

    private static final String URL_TEMPLATE = "/smartux/UXSimpleJoin";
    private static final String SUCCESS_CODE = "0000";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UxSimpleJoinSmsMapper uxSimpleJoinSmsMapper;

    @MockBean
    private UxSimpleJoinService uxSimpleJoinService;

    @Test
    @DisplayName("정상적으로 tvG 유플릭스 간편 가입 안내 SMS 요청이 성공하는지 확인")
    void whenRequestUxSimpleJoinSms_thenReturnSuccess() throws Exception {
        // given
        doNothing().when(uxSimpleJoinService).requestUxSimpleJoinSms(any());

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
        assertThat(response).contains(SUCCESS_CODE);    // 성공 코드가 있는지 확인
    }

}