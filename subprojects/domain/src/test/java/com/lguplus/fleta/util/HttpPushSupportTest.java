package com.lguplus.fleta.util;

import com.lguplus.fleta.config.HttpPushConfig;
import com.lguplus.fleta.data.dto.request.inner.HttpPushDto;
import com.lguplus.fleta.exception.httppush.HttpPushCustomException;
import com.lguplus.fleta.properties.HttpServiceProps;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HttpPushSupportTest {

    @InjectMocks
    private HttpPushSupport httpPushSupport;

    @Mock
    private HttpServiceProps httpServiceProps;


    @Test
    void testMakePushParameters() {
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("service_id", "30015");
        keyMap.put("service_pwd", "lguplusuflix");

        given(httpServiceProps.findMapByServiceId(anyString())).willReturn(Optional.of(keyMap));
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1115", "서비스ID 확인 불가"));

        Map<String, Object> rstMap = httpPushSupport.makePushParameters("lguplushdtvgcm", "30015", "G", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", null);

        assertThat(rstMap).containsEntry("APPLICATION_ID", "lguplushdtvgcm");
    }

    @Test
    void testServiceIdNotFoundExceptionOne() {
        HttpPushCustomException httpPushCustomException = new HttpPushCustomException();
        httpPushCustomException.setCode("1115");
        httpPushCustomException.setMessage("서비스ID 확인 불가");

//        given(httpServiceProps.findMapByServiceId(anyString())).willThrow(httpPushCustomException);
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1115", "서비스ID 확인 불가"));

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            httpPushSupport.makePushParameters("lguplushdtvgcm", "notexist_service_id", "G", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", null);
        });

        assertThat(exception.getMessage()).isEqualTo("서비스ID 확인 불가");
    }

    @Test
    void testServiceIdNotFoundExceptionTwo() {
        HttpPushCustomException httpPushCustomException = new HttpPushCustomException();
        httpPushCustomException.setCode("1115");
        httpPushCustomException.setMessage("서비스ID 확인 불가");

        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1115", "서비스ID 확인 불가"));

        Map<String, String> serviceMap = new HashMap<>();
        serviceMap.put("service_pwd", null);

        given(httpServiceProps.findMapByServiceId(anyString())).willReturn(Optional.of(serviceMap));

        HttpPushCustomException exception = assertThrows(HttpPushCustomException.class, () -> {
            httpPushSupport.makePushParameters("lguplushdtvgcm", "notexist_service_id", "G", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", null);
        });

        assertThat(exception.getMessage()).isEqualTo("서비스ID 확인 불가");
    }

    @Test
    void testApnPayload() {
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("service_id", "30015");
        keyMap.put("service_pwd", "lguplusuflix");

        given(httpServiceProps.findMapByServiceId(anyString())).willReturn(Optional.of(keyMap));
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1115", "서비스ID 확인 불가"));

        Map<String, Object> rstMap = httpPushSupport.makePushParameters("lguplushdtvgcm", "30015", "A", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", List.of("badge!^1", "sound!^ring.caf", "cm!^aaaa"));

        assertThat(rstMap).containsEntry("APPLICATION_ID", "lguplushdtvgcm");
    }

    @Test
    void testException() {
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            HttpPushSupport httpPushSupport = new HttpPushSupport(new HttpServiceProps(new HttpPushConfig.HttpPushExceptionCode(), new HttpPushConfig.HttpPushExceptionMessage()));

            Method method = httpPushSupport.getClass().getDeclaredMethod("encryptServicePassword", String.class);
            method.setAccessible(true);

            method.invoke(httpPushSupport, (Object) null);
        });

        assertThat(exception).isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void testMakePushMap() {
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            HttpPushSupport httpPushSupport = new HttpPushSupport(new HttpServiceProps(new HttpPushConfig.HttpPushExceptionCode(), new HttpPushConfig.HttpPushExceptionMessage()));

            Method method = httpPushSupport.getClass().getDeclaredMethod("makePushMap", HttpPushDto.class, String.class, String.class);
            method.setAccessible(true);

            HttpPushDto httpPushDto = HttpPushDto.builder()
                    .payload("보낼 메시지")
                    .build();

            method.invoke(httpPushSupport, httpPushDto, "", "");
        });

        assertThat(exception).isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void testSingleTransactionIdNum() {
        JunitTestUtils.setValue(httpServiceProps, "singleTransactionIDNum", new AtomicInteger(10000));

        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("service_id", "30015");
        keyMap.put("service_pwd", "lguplusuflix");

        given(httpServiceProps.findMapByServiceId(anyString())).willReturn(Optional.of(keyMap));
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1115", "서비스ID 확인 불가"));

        Map<String, Object> rstMap = httpPushSupport.makePushParameters("lguplushdtvgcm", "30015", "G", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", null);

        assertThat(rstMap).containsEntry("APPLICATION_ID", "lguplushdtvgcm");
    }

    @Test
    void testNegativeApnPayload() {
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("service_id", "30015");
        keyMap.put("service_pwd", "lguplusuflix");

        given(httpServiceProps.findMapByServiceId(anyString())).willReturn(Optional.of(keyMap));
        given(httpServiceProps.getExceptionCodeMessage(anyString())).willReturn(Pair.of("1115", "서비스ID 확인 불가"));

        Map<String, Object> rstMap = httpPushSupport.makePushParameters("lguplushdtvgcm", "30015", "A", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", List.of("badge1"));

        assertThat(rstMap).containsEntry("APPLICATION_ID", "lguplushdtvgcm");
    }

}