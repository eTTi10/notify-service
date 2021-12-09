package com.lguplus.fleta.api.outer.latest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.LatestDto;
import com.lguplus.fleta.data.dto.response.GenericRecordsetResponseDto;
import com.lguplus.fleta.data.entity.LatestEntity;
import com.lguplus.fleta.data.mapper.LatestSearchRequestMapper;
import com.lguplus.fleta.service.latest.LatestService;
import com.lguplus.fleta.util.JunitTestUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {LatestController.class
        , ArgumentResolverConfig.class
        , MessageConverterConfig.class})
@Slf4j
class LatestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LatestService latestService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LatestSearchRequestMapper latestSearchRequestMapper;

    @BeforeEach
    void setUp() throws Exception {

        LatestEntity rs1 = new LatestEntity();
        JunitTestUtils.setValue(rs1, "saId", "500058151453");
        JunitTestUtils.setValue(rs1, "mac", "001c.627e.039c");
        JunitTestUtils.setValue(rs1, "ctn", "01055805424");
        JunitTestUtils.setValue(rs1, "catId", "T3021");
        JunitTestUtils.setValue(rs1, "regId", "500023630832");
        JunitTestUtils.setValue(rs1, "catName", "놀라운 대회 스타킹");
        JunitTestUtils.setValue(rs1, "rDate", "2014-11-09 13:18:14.000");
        JunitTestUtils.setValue(rs1, "categoryGb", "");

        List<LatestEntity> rs = List.of(rs1);
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

    @Test
    @DisplayName("LatestControllerTest.getLatestList 정상적으로 리스트 데이터를 수신하는지 확인")
    void getLatestList() throws Exception {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("saId", "500058151453");
        params.add("mac", "001c.627e.039c");
        params.add("ctn", "01055805424");
        params.add("catId", "T3021");

        MvcResult mvcResult = mockMvc.perform(get("/comm/latest")
                        .accept(MediaType.APPLICATION_JSON)
                        .queryParams(params)
                ).andExpect(status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        assertThat(status).isEqualTo(200);
        assertThat(responseString).contains("0000");

        log.info("RESULT >> ["+responseString+"]");
        log.info("LatestControllerTest End");
    }

}
