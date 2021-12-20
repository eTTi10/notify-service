package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
/*
@Mock
@MockBean
@Spy
@SpyBean
@InjectMocks
 */

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith({ MockitoExtension.class})
class PushSingleDomainServiceTest {

    @InjectMocks
    PushSingleDomainService pushSingleDomainService;

    //@Mock
    private PushConfig pushConfig;

    @Mock
    private PushSingleClient pushSingleClient;

    PushRequestSingleDto pushRequestSingleDto;

    @BeforeEach
    void setUp() {

        SetupProperties();

        pushSingleDomainService = new PushSingleDomainService(pushConfig, pushSingleClient);

        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        pushRequestSingleDto = PushRequestSingleDto.builder()
                .serviceId("lguplushdtvgcm")
                .pushType("G")
                .appId("30011")
                .regId("-")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();
    }

    void SetupProperties() {
        StandardEnvironment environment = mock(StandardEnvironment.class);

        MutablePropertySources mutablePropertySources = mock(MutablePropertySources.class);
        given(environment.getPropertySources()).willReturn(mutablePropertySources);

        PropertiesPropertySource propertySource = mock(PropertiesPropertySource.class);
        given(mutablePropertySources.get(anyString())).willReturn((PropertySource) propertySource);

        Map<String, Object> map = new HashMap<>();
        //map.put("error.flag.com.lguplus.fleta.exception.ParameterTypeMismatchException", "5008");
        map.put("push-comm.push.delay.reqCnt", "100");
        map.put("push-comm.push.call.retryCnt", "2");
        map.put("push-comm.retry.exclud.codeList", "202|400|401|403|404|410|412|5102");

        map.put("push-comm.push.old.lgupush.notiType", "POS");
        map.put("push-comm.push.old.lgupush.pushAppId", "smartux0001");

        map.put("push-service.key7.push.service_id", "30011");
        map.put("push-service.key7.push.service_pwd", "lguplushdtvgcm");
        map.put("push-service.key8.push.service_id", "30015");
        map.put("push-service.key8.push.service_pwd", "lguplusuflix");
        map.put("push-service.key9.push.service_id", "30021");
        map.put("push-service.key9.push.service_pwd", "lguplushdtvapns");

        map.put("push-service.key12.push.service_id", "00007");
        map.put("push-service.key12.push.service_pwd", "smartux");
        map.put("push-service.key12.push.linkage_type", "LGUPUSH_OLD");
        map.put("error.message.9999", "기타 에러");
        map.put("error.message.5000", "필수 요청 정보 누락");
        given(propertySource.getSource()).willReturn(map);

        pushConfig = new PushConfig(environment);
    }

    //@Test
    void initialize() {
    }

    @Test
    void requestPushSingle() {
        //normal case
        given( pushConfig.getServicePassword(anyString()) ).willReturn("--password--");
        given( pushConfig.getServiceLinkType(anyString()) ).willReturn("LGUPUSH_OLD");

        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

        pushSingleDomainService.initialize();

        PushClientResponseDto responseDto = pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        Assertions.assertTrue("200".equals(responseDto.getCode()));

    }

}