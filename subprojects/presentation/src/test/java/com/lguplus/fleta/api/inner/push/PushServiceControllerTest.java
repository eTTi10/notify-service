package com.lguplus.fleta.api.inner.push;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lguplus.fleta.config.ArgumentResolverConfig;
import com.lguplus.fleta.config.MessageConverterConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.mapper.PushRequestMapper;
import com.lguplus.fleta.service.push.PushAnnouncementService;
import com.lguplus.fleta.service.push.PushMultiService;
import com.lguplus.fleta.service.push.PushSingleService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@WebMvcTest
@ContextConfiguration(classes = {PushServiceController.class
    , ArgumentResolverConfig.class
    , MessageConverterConfig.class})
@Slf4j
class PushServiceControllerTest {

    private final ObjectMapper MAPPER = new ObjectMapper()
        .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    PushServiceController pushServiceController1;
    //@Autowired
    private MockMvc mvc;
    @MockBean
    private PushSingleService pushSingleService;
    @MockBean
    private PushAnnouncementService pushAnnouncementService;
    @MockBean
    private PushMultiService pushMultiService;
    @MockBean
    private PushRequestMapper pushRequestMapper;
    //.registerModule(new JavaTimeModule());

    @BeforeEach
    void setUp() {
        pushServiceController1 = new PushServiceController(pushAnnouncementService, pushSingleService, pushMultiService, pushRequestMapper);
        ReflectionTestUtils.setField(pushServiceController1, "pushRejectRegList", Set.of("REJECT_USER1"));
        this.mvc = MockMvcBuilders.standaloneSetup(pushServiceController1).build();

        // Mock Dto
        PushClientResponseDto dto = PushClientResponseDto.builder().build();

        // Mock Method
        given(pushAnnouncementService.requestAnnouncement(any())).willReturn(dto);

        given(pushRequestMapper.toDtoSingle(any())).willReturn(PushRequestSingleDto.builder().regId("test").build());

        // PushRequestSingleDto dto = pushRequestMapper.toDtoSingle(pushRequestBodySingleVo);
    }

    @Test
    @DisplayName("Push Announce 테스트")
    void pushRequestAnnouncement() throws Exception {

        List<String> list = new ArrayList<>();
        list.add("badge!^1");
        list.add("sound!^ring.caf");
        list.add("cm!^aaaa");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("app_id", "lguplushdtvgcm");
        queryParams.put("push_type", "G");
        queryParams.put("service_id", "30011");
        queryParams.put("msg", "\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"");
        queryParams.put("items", list);

        String requestContent = MAPPER.writeValueAsString(queryParams);

        log.debug("===: " + requestContent);

        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.post("/notify/push/announcement")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(requestContent)
            ).andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        //System.out.println("TEST >> ["+responseString+"]");
        log.debug("TEST >> [" + responseString + "]");
        Assertions.assertThat(status).isEqualTo(200);
    }

    @Test
    @DisplayName("Single Push 테스트")
    void pushRequest() throws Exception {

        List<String> list = new ArrayList<>();
        list.add("badge!^1");
        list.add("sound!^ring.caf");
        list.add("cm!^aaaa");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("app_id", "lguplusuflix");
        queryParams.put("push_type", "G");
        queryParams.put("reg_id", "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        queryParams.put("service_id", "30015");
        queryParams.put("msg", "\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"");
        queryParams.put("items", list);

        String requestContent = MAPPER.writeValueAsString(queryParams);

        log.debug("===: " + requestContent);

        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.post("/notify/push/single")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(requestContent)
            ).andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        //System.out.println("TEST >> ["+responseString+"]");
        log.debug("TEST >> [" + responseString + "]");
        Assertions.assertThat(status).isEqualTo(200);

    }

    @Test
    @DisplayName("RegId reject 테스트")
    void pushRequestRegId() throws Exception {
        List<String> list = new ArrayList<>();
        list.add("badge!^1");
        list.add("sound!^ring.caf");
        list.add("cm!^aaaa");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("app_id", "lguplusuflix");
        queryParams.put("push_type", "G");
        queryParams.put("reg_id", "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");
        queryParams.put("service_id", "30015");
        queryParams.put("msg", "\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"");
        queryParams.put("items", list);

        String requestContent = MAPPER.writeValueAsString(queryParams);
        MvcResult mvcResult;
        given(pushRequestMapper.toDtoSingle(any())).willReturn(PushRequestSingleDto.builder().regId("REJECT_USER1").build());
        mvcResult = mvc.perform(
                MockMvcRequestBuilders.post("/notify/push/single")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(requestContent)
            ).andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        Assertions.assertThat(status).isEqualTo(mvcResult.getResponse().getStatus());
    }


    @Test
    @DisplayName("멀티 푸쉬 테스트")
    void multiPushRequest() throws Exception {

        List<String> list = new ArrayList<>();
        list.add("badge!^1");
        list.add("sound!^ring.caf");
        list.add("cm!^aaaa");

        List<String> listUser = new ArrayList<>();
        listUser.add("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=");

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("app_id", "lguplusuflix");
        queryParams.put("push_type", "G");
        queryParams.put("users", listUser);
        queryParams.put("service_id", "30015");
        queryParams.put("msg", "\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"");
        queryParams.put("items", list);

        String requestContent = MAPPER.writeValueAsString(queryParams);

        log.debug("===: " + requestContent);

        MvcResult mvcResult = mvc.perform(
                MockMvcRequestBuilders.post("/notify/push/multi")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .characterEncoding("UTF-8")
                    .content(requestContent)
            ).andExpect(status().isOk())
            .andReturn();

        MockHttpServletResponse response = mvcResult.getResponse();
        int status = response.getStatus();
        String responseString = response.getContentAsString();

        //System.out.println("TEST >> ["+responseString+"]");
        log.debug("TEST >> [" + responseString + "]");
        Assertions.assertThat(status).isEqualTo(200);
    }


}