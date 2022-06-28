package com.lguplus.fleta.api.outer.push;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.mapper.DeviceInfoPostRequestMapper;
import com.lguplus.fleta.service.push.DeviceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@ExtendWith({RestDocumentationExtension.class,MockitoExtension.class})
@WebMvcTest
@ContextConfiguration(classes = {DeviceInfoController.class
    , ArgumentResolverConfig.class
    , MessageConverterConfig.class})
@Slf4j
@AutoConfigureRestDocs
class DeviceInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DeviceInfoService deviceInfoService;

    @MockBean
    private DeviceInfoPostRequestMapper deviceInfoPostRequestMapper;

    private static final String SA_ID = "500058151453";
    private static final String SERVICE_TYPE = "H";
    private static final String AGENT_TYPE = "G";
    private static final String NOTI_TYPE = "N";


    @Test
    void postDeviceInfo() throws Exception {
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("saId", SA_ID);
        params.add("service_type", SERVICE_TYPE);
        params.add("agent_type", AGENT_TYPE);
        params.add("noti_type", NOTI_TYPE);

        mockMvc.perform(post("/v1/push/deviceinfo")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk())
            .andDo(document("push/deviceinfo"));
    }

    @DisplayName("postDeviceInfo 잘못된 파라미터")
    @Test
    void postDeviceInfoBadParam() throws Exception {
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("saId", SA_ID);
        params.add("service_type", SERVICE_TYPE);
        params.add("agent_type", "a"); // wrong
        params.add("noti_type", NOTI_TYPE);

        MvcResult mvcResult = mockMvc.perform(put("/v1/push/deviceinfo")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().is4xxClientError())
            .andReturn();
        System.out.println("mvcResult = " + mvcResult);
    }
    
    @Test
    void putDeviceInfo() throws Exception {
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("saId", SA_ID);
        params.add("service_type", SERVICE_TYPE);
        params.add("agent_type", AGENT_TYPE);
        params.add("noti_type", NOTI_TYPE);

        MvcResult mvcResult = mockMvc.perform(put("/v1/push/deviceinfo")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        System.out.println("RESULT >> ["+responseString+"]");
        assertThat(status).isEqualTo(200);
        assertThat(responseString).contains("0000");

        log.info("RESULT >> ["+responseString+"]");
        log.info("DeivceInfoControllerTest.putDeviceInfo End");
    }

    @Test
    void deleteDeviceInfo() throws Exception {
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("saId", SA_ID);
        params.add("service_type", SERVICE_TYPE);
        params.add("agent_type", AGENT_TYPE);
        params.add("noti_type", NOTI_TYPE);

        MvcResult mvcResult = mockMvc.perform(delete("/v1/push/deviceinfo")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        System.out.println("RESULT >> ["+responseString+"]");
        assertThat(status).isEqualTo(200);
        assertThat(responseString).contains("0000");

        log.info("RESULT >> ["+responseString+"]");
        log.info("DeivceInfoControllerTest.deleteDeviceInfo End");
    }
}