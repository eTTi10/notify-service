package com.lguplus.fleta.service.push;

import com.lguplus.fleta.client.PushSingleClient;
import com.lguplus.fleta.config.PushConfig;
import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushClientResponseDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.NotifyPushRuntimeException;
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

    @BeforeEach
    void setUp() {

        SetupProperties();

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
                .appId("lguplushdtvgcm")
                .regId("-")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();

        ReflectionTestUtils.setField(pushSingleDomainService, "pushDelayReqCnt", "100");
        ReflectionTestUtils.setField(pushSingleDomainService, "pushCallRetryCnt", "2");
        ReflectionTestUtils.setField(pushSingleDomainService, "retryExcludeCodeList", "400|401|403");
        ReflectionTestUtils.setField(pushSingleDomainService, "oldLgPushAppId", "smartux0001");
        ReflectionTestUtils.setField(pushSingleDomainService, "oldLgPushNotiType", "POS");
        ReflectionTestUtils.setField(pushSingleDomainService, "lgPushServceId", "00007");

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

    @Test
    void requestPushSingle() {
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
        Assertions.assertTrue("200".equals(responseDto.getCode()));

    }

    @Test
    void requestPushSingle_password_null() {
        PushRequestSingleDto pushRequestSingleDto1 = PushRequestSingleDto.builder()
                .serviceId("XXXXX")
                .pushType("G")
                .appId("lguplushdtvgcm")
                .regId("-")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();
        Exception thrown = assertThrows(ServiceIdNotFoundException.class, () -> {
            pushSingleDomainService.requestPushSingle(pushRequestSingleDto1);
        });

        assertEquals(thrown.getClass().getName(), "com.lguplus.fleta.exception.push.ServiceIdNotFoundException");
    }

    @Test
    void requestPushSingle_isLgPush() {

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
        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("200").build());

        PushRequestSingleDto pushRequestSingleDto1 = PushRequestSingleDto.builder()
                .serviceId("00007")
                .pushType("G")
                .appId("lguplushdtvgcm")
                .regId("-")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();
        //pushSingleDomainService.requestPushSingle(pushRequestSingleDto1);

        PushClientResponseDto responseDto = pushSingleDomainService.requestPushSingle(pushRequestSingleDto1);
        Assertions.assertTrue("200".equals(responseDto.getCode()));
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

        int count = 0;
        for(String code : codeList) {
            given( pushSingleClient.requestPushSingle(anyMap()) ).willReturn(PushResponseDto.builder().statusCode(code).build());

            Exception thrown = assertThrows(NotifyPushRuntimeException.class, () -> {
                pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
            });

            boolean isNotiPush = thrown instanceof NotifyPushRuntimeException;
            if(isNotiPush)
                count ++;
        }
        assertEquals(count, codeList.size());
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
        Assertions.assertTrue("200".equals(responseDto.getCode()));
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

        Exception thrown = assertThrows(MaxRequestOverException.class, () -> {
            pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        });

        assertEquals(thrown.getClass().getName(), "com.lguplus.fleta.exception.push.MaxRequestOverException");

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

        Exception thrown = assertThrows(MaxRequestOverException.class, () -> {
            pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        });

        assertEquals(thrown.getClass().getName(), "com.lguplus.fleta.exception.push.MaxRequestOverException");

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
        Assertions.assertTrue("200".equals(responseDto.getCode()));
    }

    //ReflectionTestUtils.setField(pushSingleDomainService, "retryExcludeCodeList", "A|B|C");


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
        Assertions.assertTrue("200".equals(responseDto.getCode()));
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
        Assertions.assertTrue("200".equals(responseDto.getCode()));


        PushRequestSingleDto pushRequestSingleDto1 = PushRequestSingleDto.builder()
                .serviceId("00007")
                .pushType("G")
                .appId("lguplushdtvgcm")
                .regId("-")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();

        PushClientResponseDto responseDto1 = pushSingleDomainService.requestPushSingle(pushRequestSingleDto1);
        Assertions.assertTrue("200".equals(responseDto1.getCode()));
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
        Exception thrown = assertThrows(NotExistRegistIdException.class, () -> {
            pushSingleDomainService.requestPushSingle(pushRequestSingleDto);
        });
        assertEquals(thrown.getClass().getName(), "com.lguplus.fleta.exception.push.NotExistRegistIdException");

        given( pushSingleClient.requestPushSingle(any()) ).willReturn(PushResponseDto.builder().statusCode("400").build());
        thrown = assertThrows(BadRequestException.class, () -> pushSingleDomainService.requestPushSingle(pushRequestSingleDto));
        assertEquals(thrown.getClass().getName(), "com.lguplus.fleta.exception.push.BadRequestException");
    }




}