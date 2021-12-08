package com.lguplus.fleta.provider.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.response.inner.PushAnnouncementResponseDto;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PushAnnounceDomainFeignClientTest {

    @InjectMocks
    private PushAnnounceDomainFeignClient pushAnnounceDomainFeignClient;

    @Mock
    private PushConfig pushConfig;

    @Mock
    private PushAnnounceFeignClient pushAnnounceFeignClient;

    @Mock
    private ObjectMapper objectMapper;

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
    void requestAnnouncement() {

        Map<String, Object>  retMap = new HashMap<>();
        Map<String, Object>  contMap = new HashMap<>();
        retMap.put("response", contMap);
        contMap.put("msg_id", "-");
        contMap.put("push_id", "-");
        contMap.put("status_msg", "-");
        contMap.put("status_code", "200");

        given( pushAnnounceFeignClient.requestAnnouncement(any(), anyMap()) ).willReturn(retMap);

        PushAnnouncementResponseDto responseDto = pushAnnounceDomainFeignClient.requestAnnouncement(paramMap);
        Assertions.assertTrue("200".equals(responseDto.getStatusCode()));

    }

}