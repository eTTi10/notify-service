package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.NotifyRuntimeException;
import com.lguplus.fleta.exception.push.BadRequestException;
import com.lguplus.fleta.exception.push.MaxRequestOverException;
import com.lguplus.fleta.exception.push.NotExistRegistIdException;
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

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith({ MockitoExtension.class})
class PushSingleDomainServiceTest {

    @InjectMocks
    PushSingleDomainService pushSingleDomainService;

    private PushConfig pushConfig;

    @Mock
    private PushSingleClient pushSingleClient;

    PushRequestSingleDto pushRequestSingleDto;

    List<String> items;
    List<PushRequestItemDto> addItems;

    @BeforeEach
    void setUp() {

        SetupProperties();

        addItems = new ArrayList<>();
        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        pushSingleDomainService = new PushSingleDomainService(pushConfig, pushSingleClient);

        items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");
        items.add("cm2aaaa");
        items.add("cm1!^aaaa!^aaaa");

        pushRequestSingleDto = PushRequestSingleDto.builder()
                .serviceId("30011")
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .regId("-")
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .retryCount(0)
                .build();

        ReflectionTestUtils.setField(pushSingleDomainService, "pushDelayReqCnt", 100);
        ReflectionTestUtils.setField(pushSingleDomainService, "pushCallRetryCnt", 2);
        ReflectionTestUtils.setField(pushSingleDomainService, "retryExcludeCodeList", Set.of("400", "401", "403"));
        ReflectionTestUtils.setField(pushSingleDomainService, "oldLgPushAppId", "smartux0001");
        ReflectionTestUtils.setField(pushSingleDomainService, "oldLgPushNotiType", "POS");
        ReflectionTestUtils.setField(pushSingleDomainService, "lgPushServceId", "00007");
        ReflectionTestUtils.setField(pushSingleDomainService, "pushProgressCnt", new ArrayList<>());

        pushSingleDomainService.initialize();
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

    @Test
    void requestPushSingle_normal() {
        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("200").build());
        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );

        PushClientResponseDto responseDto = pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        Assertions.assertEquals("200", responseDto.getCode());

        PushRequestSingleDto pushRequestSingleDtoLg = PushRequestSingleDto.builder()
                .serviceId("00007")
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .regId("-")
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .retryCount(5)
                .build();
        //pushSingleDomainService.requestPushSingle(pushRequestSingleDto1);

        PushClientResponseDto responseDtoLg = pushSingleDomainService.requestPushSingle(pushRequestSingleDtoLg);
        Assertions.assertEquals("200", responseDtoLg.getCode());
    }

    @Test
    void requestPushSingle_password_null() {

        PushRequestSingleDto pushRequestSingleDto1 = PushRequestSingleDto.builder()
                .serviceId("XXXXX") //unknown service id
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .regId("-")
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();
        assertThrows(ServiceIdNotFoundException.class, () -> pushSingleDomainService.requestPushSingle(pushRequestSingleDto1));
    }

    @Test
    void requestPushSingle_exception() {

        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );

        List<String> codeList = Arrays.asList(new String[]{"202", "400", "401","403", "404", "410","412", "500", "502","503", "5102", "5103", "Unknown"});//, "-"});

        for(String code : codeList) {
            given( pushSingleClient.requestPushSingle(anyMap()) ).willReturn(PushResponseDto.builder().statusCode(code).build());
            assertThrows(NotifyRuntimeException.class, () -> pushSingleDomainService.requestPushSingle(pushRequestSingleDto));
        }
    }

    @Test
    void requestPushSingle_retryExcludeCode() {

        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );
        //ReflectionTestUtils.setField(pushSingleDomainService, "retryExcludeCodeList", "400|401|403");
        //BadRequestException, UnAuthorizedException, ForbiddenException

        given( pushSingleClient.requestPushSingle(anyMap()) ).willReturn(PushResponseDto.builder().statusCode("400").build());
        assertThrows(NotifyRuntimeException.class, () -> pushSingleDomainService.requestPushSingle(pushRequestSingleDto));

        given( pushSingleClient.requestPushSingle(anyMap()) ).willReturn(PushResponseDto.builder().statusCode("202").build());
        assertThrows(NotifyRuntimeException.class, () -> pushSingleDomainService.requestPushSingle(pushRequestSingleDto));
    }

    @Test
    void requestPushSingle_abnormalTime() {
        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("200").build());
        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis()-4000)
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );

        PushClientResponseDto responseDto = pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        Assertions.assertEquals("200", responseDto.getCode());
    }

    @Test
    void requestPushSingle_interval_over() {
        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(100 * 3)// 100 초과
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis()-100)
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );

        assertThrows(MaxRequestOverException.class, () -> {
            pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        });

    }

    @Test
    void requestPushSingle_interval_over1() {
        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(110)// 100 초과
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis()-100)
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );

        assertThrows(MaxRequestOverException.class, () -> {
            pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        });

    }

    @Test
    void requestPushSingle_interval_reset() {
        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)// 100 초과
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis()-1100)//측정시간 -1초
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );
        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

        PushClientResponseDto responseDto = pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        Assertions.assertEquals("200", responseDto.getCode());
    }

    @Test
    void requestPushSingle_isRetryExcludeCodeTrue() {
        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)// 100 초과
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis()-1100)//측정시간 -1초
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );
        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

        PushClientResponseDto responseDto = pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        Assertions.assertEquals("200",responseDto.getCode());
    }

    @Test
    void requestPushSingle_getTransactionId_0() {

        ReflectionTestUtils.setField(pushSingleDomainService, "tranactionMsgId1", new AtomicInteger(9999));
        ReflectionTestUtils.setField(pushSingleDomainService, "tranactionMsgId2", new AtomicInteger(9999));
        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)// 100 초과
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis()-500)//측정시간 -1초
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );
        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

        PushClientResponseDto responseDto = pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        Assertions.assertEquals("200", responseDto.getCode());


        PushRequestSingleDto pushRequestSingleDto1 = PushRequestSingleDto.builder()
                .serviceId("00007")
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .regId("-")
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .retryCount(0)
                .build();

        PushClientResponseDto responseDto1 = pushSingleDomainService.requestPushSingle(pushRequestSingleDto1);
        Assertions.assertEquals("200", responseDto1.getCode());
    }

    @Test
    void requestPushSingle_retry() {

        given( pushSingleClient.getPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)// 100 초과
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis()-500)//측정시간 -1초
                .build()
        );
        given( pushSingleClient.putPushStatus(anyString(), anyLong(), anyLong()) ).willReturn(PushStatDto.builder().serviceId(pushRequestSingleDto.getServiceId())
                .measurePushCount(0)
                .measureIntervalMillis(1000L)
                .measureStartMillis(System.currentTimeMillis())
                .build()
        );

        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("410").build());
        assertThrows(NotExistRegistIdException.class, () -> pushSingleDomainService.requestPushSingle(pushRequestSingleDto));

        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("400").build());
        assertThrows(BadRequestException.class, () -> pushSingleDomainService.requestPushSingle(pushRequestSingleDto));
    }




}