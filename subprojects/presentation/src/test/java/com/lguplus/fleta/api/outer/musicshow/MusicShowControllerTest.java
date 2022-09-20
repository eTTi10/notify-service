package com.lguplus.fleta.api.outer.musicshow;

import com.lguplus.fleta.data.dto.GetPushResponseDto;
import com.lguplus.fleta.data.dto.PostPushResponseDto;
import com.lguplus.fleta.service.musicshow.MusicShowService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {MusicShowController.class})
@DisplayName("MusicShowController 테스트 ")
class MusicShowControllerTest {

    private static final String SUCCESS_CODE = "0000";
    private static final String URL_TEMPLATE = "/videolte/musicshow/push";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MusicShowService service;

    MultiValueMap<String, String> getValidQueryParams01() {
        MultiValueMap<String, String> uriVars = new LinkedMultiValueMap<>();
        uriVars.add("sa_id", "500154347307");
        uriVars.add("stb_mac", "v001.5434.7307");
        uriVars.add("album_id", "M0118C3162PPV00");
        return uriVars;
    }

    MultiValueMap<String, String> getValidQueryParams02() {
        MultiValueMap<String, String> uriVars = new LinkedMultiValueMap<>();
        uriVars.add("sa_id", "1000494369123456");
        uriVars.add("stb_mac", "v010.0049.");
        uriVars.add("album_id", "M01198F334PPV00ㄱ");
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

    @Test
    @DisplayName("파라미터 에러 조회 테스트")
    void getPush_NoData() throws Exception {
        GetPushResponseDto responseDto = GetPushResponseDto.builder().build();
        // Mock Method
        given(service.getPush(any())).willReturn(responseDto);

        MultiValueMap<String, String> queryParams = getValidQueryParams02();
        MvcResult mvcResult = mockMvc.perform(get(URL_TEMPLATE).queryParams(queryParams))
            .andExpect(status().isBadRequest())
            .andReturn();

    }


    MultiValueMap<String, String> getValidQueryParams03() {
        MultiValueMap<String, String> uriVars = new LinkedMultiValueMap<>();
        uriVars.add("sa_id", "1000494369");
        uriVars.add("stb_mac", "v010.0049.4369");
        uriVars.add("album_id", "M01198F334PPV00");
        uriVars.add("category_id", "E967O");
        uriVars.add("album_nm", "더쇼");
        uriVars.add("start_dt", "202201211214");
        return uriVars;
    }

    @Test
    @DisplayName("정상 등록 테스트")
    void registerPush() throws Exception {

        PostPushResponseDto responseDto = PostPushResponseDto.builder().build();
        // Mock Method
        given(service.postPush(any())).willReturn(responseDto);

        MultiValueMap<String, String> queryParams = getValidQueryParams03();
        MvcResult mvcResult = mockMvc.perform(post(URL_TEMPLATE).queryParams(queryParams))
            .andExpect(status().isOk())
            .andReturn();

        //        String response = mvcResult.getResponse().getContentAsString();
        //        assertThat(response).contains(SUCCESS_CODE);    // 성공 코드가 있는지 확인

    }

    MultiValueMap<String, String> getValidQueryParams04() {
        MultiValueMap<String, String> uriVars = new LinkedMultiValueMap<>();
        uriVars.add("sa_id", "500068218317");
        uriVars.add("stb_mac", "v000.6821.8317");
        uriVars.add("album_id", "M0118C3162PPV00");
        return uriVars;
    }

    @Test
    @DisplayName("정상 삭제 테스트")
    void deletePush() throws Exception {
        PostPushResponseDto responseDto = PostPushResponseDto.builder().build();
        // Mock Method
        given(service.releasePush(any())).willReturn(responseDto);

        MultiValueMap<String, String> queryParams = getValidQueryParams04();
        MvcResult mvcResult = mockMvc.perform(delete(URL_TEMPLATE).queryParams(queryParams))
            .andExpect(status().isOk())
            .andReturn();
    }
}