package com.lguplus.fleta.api.outer.push;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.restdocs.cli.CliDocumentation.curlRequest;
import static org.springframework.restdocs.headers.HeaderDocumentation.responseHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.lguplus.fleta.RestDocsConfig;
import com.lguplus.fleta.advice.exhandler.OuterControllerAdvice;
import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.mapper.DeviceInfoPostRequestMapper;
import com.lguplus.fleta.exhandler.ErrorResponseResolver;
import com.lguplus.fleta.service.push.DeviceInfoService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.snippet.Snippet;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@ExtendWith({RestDocumentationExtension.class,MockitoExtension.class})
@WebMvcTest
@ContextConfiguration(classes = {DeviceInfoController.class
    , ErrorResponseResolver.class
    , OuterControllerAdvice.class
    , ArgumentResolverConfig.class
    , MessageConverterConfig.class})
@Slf4j
@Import(RestDocsConfig.class)
@AutoConfigureMockMvc
@AutoConfigureRestDocs
class DeviceInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DeviceInfoController deviceInfoController;
    @MockBean
    private DeviceInfoService deviceInfoService;

    @MockBean
    private DeviceInfoPostRequestMapper deviceInfoPostRequestMapper;

    @InjectMocks
    private OuterControllerAdvice outerControllerAdvice;
    @Mock
    private ErrorResponseResolver errorResponseResolver;


    private static final String SA_ID = "500058151453";
    private static final String SERVICE_TYPE = "H";
    private static final String AGENT_TYPE = "G";
    private static final String NOTI_TYPE = "N";

    @Test
    void postDeviceInfo() throws Exception {
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("sa_id", SA_ID);
        params.add("service_type", SERVICE_TYPE);
        params.add("agent_type", AGENT_TYPE);
        params.add("noti_type", NOTI_TYPE);

        mockMvc.perform(RestDocumentationRequestBuilders.post("/v1/push/deviceinfo")
                .content(
                    " {\"sa_id\" : \"500058151453\" ,"
                        + "\n\"service_type\" : \"A\" ,"
                        + "\n\"agent_type\" : \"G\" ,"
                        + "\n\"noti_type\" = \"N\" }")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk())
            .andDo(document("{methodName}", simpleRequestParameterSnippet()));
    }

    private Snippet simpleRequestParameterSnippet() {
        return requestParameters(parameterWithName("sa_id").description("가입번호 \n자리수: 12\nex) 500058151453"),
            parameterWithName("service_type").description("service_type \n자리수: 1\n ex) H : HDTV / U : 유플릭스 /  C : 뮤직공연 / R : VR / G : 골프 / D : 게임방송 / B : 프로야구 / K : 아이들나라, example=H"),
            parameterWithName("agent_type").description("agent_type \n자리수: 1\nex) G:GCM, A:APNS, example=G"),
            parameterWithName("noti_type").description("noti_type \n자리수: 1\nex) A:전체받기/ S:구독만받기 / N:푸시 안받기, example=N"));

    }

    @DisplayName("postDeviceInfo 잘못된 파라미터") // 최종적으로는 200 9999에러 리스폰스인뎀.
    @Test
    void postDeviceInfoBadParam() throws Exception {
//        mockMvc =  MockMvcBuilders.webAppContextSetup(webApplicationContext)
//            .apply(documentationConfiguration(restDocumentation))
//
//            .setControllerAdvice(outerControllerAdvice)  // ExceptionHandler 등록
//            .build();
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("sa_id", SA_ID);
        params.add("service_type", SERVICE_TYPE);
        params.add("agent_type", "a"); // wrong
        params.add("noti_type", NOTI_TYPE);

        MvcResult mvcResult = (MvcResult) mockMvc.perform(put("/v1/push/deviceinfo").content(
            " {\"sa_id\" : \"500058151453\" ,"
                            + "\n\"service_type\" : \"a\" ,"
                            + "\n\"agent_type\" : \"G\" ,"
                            + "\n\"noti_type\" = \"N\" }")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk())
           .andDo(document("{methodName}")).andReturn();
        System.out.println("mvcResult = " + mvcResult);
    }
    
    @Test
    void putDeviceInfo() throws Exception {
        MultiValueMap<String , String> params = new LinkedMultiValueMap<>();
        params.add("sa_id", SA_ID);
        params.add("service_type", SERVICE_TYPE);
        params.add("agent_type", AGENT_TYPE);
        params.add("noti_type", NOTI_TYPE);

        MvcResult mvcResult = (MvcResult) mockMvc.perform(put("/v1/push/deviceinfo").content(
                    "{\"sa_id\" : \"500058151453\" ,"
                        + "\n\"service_type\" : \"H\" ,"
                        + "\n\"agent_type\" : \"G\" ,"
                        + "\n\"noti_type\" = \"N\" }")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk()).andDo(document("{methodName}")).andReturn();


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
        params.add("sa_id", SA_ID);
        params.add("service_type", SERVICE_TYPE);
        params.add("agent_type", AGENT_TYPE);
        params.add("noti_type", NOTI_TYPE);

        MvcResult mvcResult = (MvcResult) mockMvc.perform(delete("/v1/push/deviceinfo").content(
                    "{\"sa_id\" : \"500058151453\" ,"
                        + "\n\"service_type\" : \"H\" ,"
                        + "\n\"agent_type\" : \"G\" ,"
                        + "\n\"noti_type\" = \"N\" }")
                .accept(MediaType.APPLICATION_JSON)
                .queryParams(params)
            ).andExpect(status().isOk()).andDo(document("{methodName}")).andReturn();

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