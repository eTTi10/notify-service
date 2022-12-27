package com.lguplus.fleta.api.outer.latest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.entity.Latest;
import com.lguplus.fleta.data.mapper.LatestPostRequestMapper;
import com.lguplus.fleta.data.mapper.LatestSearchRequestMapper;
import com.lguplus.fleta.service.latest.LatestService;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {LatestController.class
    , ArgumentResolverConfig.class
    , MessageConverterConfig.class})
@Slf4j
class LatestControllerTest {

    private static final String MAC = "001c.627e.039c";
    private static final String CTN = "01055805424";
    private static final String CAT_ID = "T3021";
    private static final String REG_ID = "500058151453";
    private static final String CAT_NAME = "놀라운 대회 스타킹";
    private static final String R_DATE = "2014-11-09 13:18:14.000";
    private static final String CATEGORY_GB = "JUN";
    private static final String SA_ID = "500058151453";
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private LatestService latestService;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private LatestSearchRequestMapper latestSearchRequestMapper;
    @MockBean
    private LatestPostRequestMapper latestPostRequestMapper;

    @BeforeEach
    void getLatestListBefore() throws Exception {

        Latest rs1 = Latest.builder()
            .saId(SA_ID).mac(MAC)
            .ctn(CTN).catId(CAT_ID)
            .regId(REG_ID).catName(CAT_NAME)
            .rDate(new Date()).categoryGb(CATEGORY_GB)
            .build();

        List<Latest> rs = List.of(rs1);
        List<LatestDto> resultList = new ArrayList<LatestDto>();

        rs.forEach(e -> {
            LatestDto item = LatestDto.builder()
                .saId(e.getSaId())
                .mac(e.getMac())
                .ctn(e.getCtn())
                .catId(e.getCatId())
                .catName(e.getCatName())
                .rDate(e.getRDate())
                .categoryGb(e.getCategoryGb())
                .build();
            resultList.add(item);
        });

        GenericRecordsetResponseDto<LatestDto> result = GenericRecordsetResponseDto.<LatestDto>genericRecordsetResponseBuilder()
            .totalCount(resultList.size())
            .recordset(resultList)
            .build();

        given(latestService.getLatestList(any())).willReturn(result);

    }


    //####################### Start 알림등록 ######################
    @Test
    @DisplayName("LatestControllerTest.insertLatest 정상적으로 데이터를 등록하는지 확인")
    void insertLatest() throws Exception {

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("saId", SA_ID);
        params.add("mac", MAC);
        params.add("ctn", CTN);
        params.add("catId", CAT_ID);
        params.add("regId", REG_ID);
        params.add("catName", CAT_NAME);
        params.add("rDate", R_DATE);
        params.add("categoryGb", CATEGORY_GB);

        MvcResult mvcResult = mockMvc.perform(post("/smartux/comm/latest")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        System.out.println("RESULT >> [" + responseString + "]");
        assertThat(status).isEqualTo(200);
        assertThat(responseString).contains("0000");

        log.info("RESULT >> [" + responseString + "]");
        log.info("LatestControllerTest.getLatestList End");
    }
    //####################### End 알림등록 ######################


    //####################### Start 알림조회 ######################
    @Test
    @DisplayName("LatestControllerTest.getLatestList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestList() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("saId", "500058151453");
        params.add("mac", "001c.627e.039c");
        params.add("ctn", "01055805424");
        params.add("catId", "T3021");

        MvcResult mvcResult = mockMvc.perform(get("/smartux/comm/latest")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        assertThat(status).isEqualTo(200);

        log.info("RESULT >> [" + responseString + "]");
        log.info("LatestControllerTest.getLatestList End");
    }
    //####################### End 알림조회 ######################


    //####################### Start 알림삭제 ######################
    @Test
    @DisplayName("LatestControllerTest.deleteLatest 정상적으로 데이터를 삭제하는지 확인")
    void deleteLatest() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("saId", "500058151453");
        params.add("mac", "001c.627e.039c");
        params.add("ctn", "01055805424");
        params.add("catId", "T3021");

        MvcResult mvcResult = mockMvc.perform(delete("/smartux/comm/latest")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        assertThat(status).isEqualTo(200);

        log.info("RESULT >> [" + responseString + "]");
        log.info("LatestControllerTest.deleteLatest End");
    }
    //####################### End 알림삭제 ######################


}
