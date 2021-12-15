package com.lguplus.fleta.util;

import com.lguplus.fleta.data.dto.request.inner.HttpPushDto;
import com.lguplus.fleta.properties.HttpServiceProps;
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

        Map<String, Object> rstMap = httpPushSupport.makePushParameters("lguplushdtvgcm", "30015", "G", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", null);

        assertThat(rstMap.get("APPLICATION_ID")).isEqualTo("lguplushdtvgcm");
    }

    @Test
    void testApnPayload() {
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("service_id", "30015");
        keyMap.put("service_pwd", "lguplusuflix");

        given(httpServiceProps.findMapByServiceId(anyString())).willReturn(Optional.of(keyMap));

        Map<String, Object> rstMap = httpPushSupport.makePushParameters("lguplushdtvgcm", "30015", "A", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", List.of("badge!^1", "sound!^ring.caf", "cm!^aaaa"));

        assertThat(rstMap.get("APPLICATION_ID")).isEqualTo("lguplushdtvgcm");
    }

    @Test
    void testException() {
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            HttpPushSupport httpPushSupport = new HttpPushSupport(new HttpServiceProps());

            Method method = httpPushSupport.getClass().getDeclaredMethod("encryptServicePassword", String.class);
            method.setAccessible(true);

            method.invoke(httpPushSupport, (Object) null);
        });

        assertThat(exception).isInstanceOf(InvocationTargetException.class);
    }

    @Test
    void testMakePushMap() {
        Exception exception = assertThrows(InvocationTargetException.class, () -> {
            HttpPushSupport httpPushSupport = new HttpPushSupport(new HttpServiceProps());

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

        Map<String, Object> rstMap = httpPushSupport.makePushParameters("lguplushdtvgcm", "30015", "G", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", null);

        assertThat(rstMap.get("APPLICATION_ID")).isEqualTo("lguplushdtvgcm");
    }

    @Test
    void testNegativeApnPayload() {
        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("service_id", "30015");
        keyMap.put("service_pwd", "lguplusuflix");

        given(httpServiceProps.findMapByServiceId(anyString())).willReturn(Optional.of(keyMap));

        Map<String, Object> rstMap = httpPushSupport.makePushParameters("lguplushdtvgcm", "30015", "A", "\"result\":{\"noti_type\":\"PAIR\", \"name\":\"김삼순\", \"data\":{\"d1\":\"aa\",\"d2\":\"bb\"}}", "01099991234", List.of("badge1"));

        assertThat(rstMap.get("APPLICATION_ID")).isEqualTo("lguplushdtvgcm");
    }

}