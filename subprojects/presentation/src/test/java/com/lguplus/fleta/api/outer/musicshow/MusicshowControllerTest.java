package com.lguplus.fleta.api.outer.musicshow;

import com.lguplus.fleta.data.dto.GetPushResponseDto;
import com.lguplus.fleta.service.musicshow.MusicShowService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {MusicshowController.class
   })
class MusicshowControllerTest {

    private static final String SUCCESS_CODE = "0000";
    private static final String URL_TEMPLATE = "/musicshow/push";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MusicShowService service;

    MultiValueMap<String, String> getValidQueryParams01() {
        MultiValueMap<String, String> uriVars = new LinkedMultiValueMap<>();
        uriVars.add("sa_id", "1000494369");
        uriVars.add("stb_mac", "v010.0049.4369");
        uriVars.add("album_id", "M01198F334PPV00");
        return uriVars;
    }

    @Test
    @DisplayName("정상 조회 테스트")
    void getPush() throws Exception {

        GetPushResponseDto responseDto = GetPushResponseDto.builder().build();
        // Mock Method
        given(service.getPush(any())).willReturn(responseDto);

        MultiValueMap<String, String> queryParams = getValidQueryParams01();
        MvcResult mvcResult = mockMvc.perform(get(URL_TEMPLATE).queryParams(queryParams))
            .andExpect(status().isOk())
            .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        assertThat(response).contains(SUCCESS_CODE);    // 성공 코드가 있는지 확인
    }

//        @Test
//        void registerPush() {
//        }
//
//        @Test
//        void deletePush() {
//        }
}