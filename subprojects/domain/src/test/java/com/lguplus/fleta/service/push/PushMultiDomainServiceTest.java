package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushMultiClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestMultiDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseMultiDto;
import com.lguplus.fleta.data.dto.response.inner.PushMultiResponseDto;
import com.lguplus.fleta.data.mapper.PushMapper;
import com.lguplus.fleta.exception.push.ServiceIdNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith({ MockitoExtension.class})
class PushMultiDomainServiceTest {

    @InjectMocks
    PushMultiDomainService pushMultiDomainService;

    private PushConfig pushConfig;

    @Mock
    PushMultiClient pushMultiClient;

    @Mock
    private PushMapper pushMapper;

    List<String> items;
    List<PushRequestItemDto> addItems = new ArrayList<>();
    PushRequestMultiDto pushRequestMultiDto;

    @BeforeEach
    void setUp() {

        SetupProperties();

        pushMultiDomainService = new PushMultiDomainService(pushConfig, pushMultiClient, pushMapper);

        items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        pushRequestMultiDto = PushRequestMultiDto.builder()
                .serviceId("30011")
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .users(items)
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();

        ReflectionTestUtils.setField(pushMultiDomainService, "oldLgPushAppId", "smartux0001");
        ReflectionTestUtils.setField(pushMultiDomainService, "oldLgPushNotiType", "POS");
    }

    @Test
    void requestMultiPush_case_password_null() {

        assertThat(PushMultiClient.MsgType.values()).isNotEmpty();

        PushRequestMultiDto testDto = PushRequestMultiDto.builder()
                .serviceId("XXXXX") //unknown service id
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .users(items)
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();

        assertThrows(ServiceIdNotFoundException.class, () -> pushMultiDomainService.requestMultiPush(testDto));
    }

    @Test
    void requestMultiPush_normal() {
        given( pushMultiClient.requestPushMulti(any()) ).willReturn(PushMultiResponseDto.builder().statusCode("200").build());

        PushClientResponseMultiDto mockDto = PushClientResponseMultiDto.builder().code("200").build();
        given(pushMapper.toClientResponseDto(any())).willReturn(mockDto);

        PushClientResponseMultiDto responseMultiDto = pushMultiDomainService.requestMultiPush(pushRequestMultiDto);

        Assertions.assertEquals("200", responseMultiDto.getCode());
    }

    @Test
    void requestMultiPush_lgpush() {
        PushRequestMultiDto testDto = PushRequestMultiDto.builder()
                .serviceId("00007") //Lg Push
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .users(items)
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();
        given( pushMultiClient.requestPushMulti(any()) ).willReturn(PushMultiResponseDto.builder().statusCode("200").build());

        PushClientResponseMultiDto mockDto = PushClientResponseMultiDto.builder().code("200").build();
        given(pushMapper.toClientResponseDto(any())).willReturn(mockDto);

        PushClientResponseMultiDto responseMultiDto = pushMultiDomainService.requestMultiPush(testDto);

        Assertions.assertEquals("200", responseMultiDto.getCode());
    }

    void SetupProperties() {
        StandardEnvironment environment = mock(StandardEnvironment.class);

        MutablePropertySources mutablePropertySources = mock(MutablePropertySources.class);
        given(environment.getPropertySources()).willReturn(mutablePropertySources);

        PropertiesPropertySource propertySource = mock(PropertiesPropertySource.class);
        given(mutablePropertySources.get(anyString())).willReturn((PropertySource) propertySource);

        Map<String, Object> map = new HashMap<>();
        //map.put("error.flag.com.lguplus.fleta.exception.ParameterTypeMismatchException", "5008");
        map.put("push.gateway.delay.request", "100");
        map.put("push.gateway.retry.count", "2");
        map.put("push.gateway.retry.exclude[0]", "202");
        map.put("push.gateway.retry.exclude[1]", "400");
        map.put("push.gateway.retry.exclude[2]", "401");
        map.put("push.gateway.retry.exclude[3]", "403");
        map.put("push.gateway.retry.exclude[4]", "404");
        map.put("push.gateway.retry.exclude[5]", "410");
        map.put("push.gateway.retry.exclude[6]", "412");
        map.put("push.gateway.retry.exclude[7]", "5102");

        map.put("push.gateway.notiType", "POS");
        map.put("push.gateway.appId", "smartux0001");

        map.put("push.service[0].id", "30011");
        map.put("push.service[0].password", "lguplushdtvgcm");
        map.put("push.service[1].id", "30015");
        map.put("push.service[1].password", "lguplusuflix");
        map.put("push.service[2].id", "30021");
        map.put("push.service[2].password", "lguplushdtvapns");
        map.put("push.service[3].id", "00007");
        map.put("push.service[3].password", "smartux");
        map.put("push.service[3].type", "LGUPUSH_OLD");
        map.put("error.message.9999", "기타 에러");
        map.put("error.message.5000", "필수 요청 정보 누락");
        given(propertySource.getSource()).willReturn(map);

        pushConfig = new PushConfig(environment);
    }
}