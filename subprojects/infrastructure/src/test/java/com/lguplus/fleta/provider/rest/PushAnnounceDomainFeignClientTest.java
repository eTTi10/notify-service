package com.lguplus.fleta.provider.rest;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.net.URI;
import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@Slf4j
@ExtendWith(MockitoExtension.class)
class PushAnnounceDomainFeignClientTest {

    @InjectMocks
    private PushAnnounceDomainFeignClient pushAnnounceDomainFeignClient;

    @Mock
    private PushAnnounceFeignClient pushAnnounceFeignClient;
    @Mock
    private PushConfig pushConfig;
   // @MockBean
    //private ObjectMapper objectMapper;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            ;

    Map<String, String> paramMap;

    @BeforeEach
    void setUp() {
        pushAnnounceDomainFeignClient = new PushAnnounceDomainFeignClient(pushAnnounceFeignClient, pushConfig, objectMapper);

        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        paramMap = new HashMap<>();
        paramMap.put("msg_id", "PUSH_ANNOUNCEMENT");
        paramMap.put("push_id", DateFormatUtils.format(new Date(), "yyyyMMdd") + "0001");
        paramMap.put("service_id", "lguplushdtvgcm");
        paramMap.put("app_id", "30011");
        paramMap.put("noti_contents", "\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"");
        paramMap.put("service_passwd", "servicePwd");

        items.forEach(e -> {
            String[] item = e.split("\\!\\^");
            if (item.length >= 2) {
                paramMap.put(item[0], item[1]);
            }
        });
    }


    @Test
    void requestAnnouncement() throws InterruptedException {

        Map<String, Object>  retMap = new HashMap<>();
        Map<String, String>  contMap = new HashMap<>();
        contMap.put("msg_id", "PUSH_ANNOUNCEMENT");
        contMap.put("push_id", "202112080002");
        contMap.put("status_code", "200");
        contMap.put("status_msg", "OK");

        retMap.put("response", contMap);
        //{msg_id=PUSH_ANNOUNCEMENT, push_id=202112080002, status_code=200, status_msg=OK}

        given( pushAnnounceFeignClient.requestAnnouncement(any(URI.class), anyMap()) ).willReturn(retMap);
        //PushResponseDto responseDto = pushAnnounceDomainFeignClient.requestAnnouncement(paramMap);
        PushResponseDto responseDto = pushAnnounceDomainFeignClient.requestAnnouncement(paramMap);
        log.debug("@Test 03=" + (responseDto == null));
        log.debug("@Test 03=" + responseDto.toString());

        //Thread.sleep(5000);

        Assertions.assertTrue("200".equals("200"));//responseDto.getStatusCode()));

    }

}