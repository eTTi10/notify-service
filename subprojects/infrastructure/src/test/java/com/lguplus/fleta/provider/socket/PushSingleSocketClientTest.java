package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.exception.push.PushBizException;
import com.lguplus.fleta.provider.socket.pool.PushSocketInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

@Slf4j
@ExtendWith({ MockitoExtension.class})
class PushSingleSocketClientTest {

    @InjectMocks
    private PushSingleSocketClient pushSingleSocketClient;

    @Mock
    PushSocketInfo socketInfo;

    @Mock
    GenericObjectPool<PushSocketInfo> pool;

    //@Mock
    PushRequestSingleDto pushRequestSingleDto;

    Map<String, String> paramMap;

    // Service Password
    private String getSha512Pwd(String servicePwd) {
        // service_pwd : SHA512 암호화
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            digest.reset();
            digest.update(servicePwd.getBytes(StandardCharsets.UTF_8));
            return String.format("%0128x", new BigInteger(1, digest.digest()));
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("기타 오류");
        }
    }

    @BeforeEach
    void setUp() {
        List<String> items = new ArrayList<>();
        items.add("badge!^1");
        items.add("sound!^ring.caf");
        items.add("cm!^aaaa");

        pushRequestSingleDto = PushRequestSingleDto.builder()
                .serviceId("30011")
                .pushType("G")
                .appId("lguplushdtvgcm")
                .regId("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();

        PushRequestSingleDto dto = pushRequestSingleDto;

        paramMap = new HashMap<>();
        paramMap.put("msg_id", "PUSH_NOTI");
        paramMap.put("push_id", "202112200001");
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getAppId());
        paramMap.put("noti_contents", dto.getMsg());
        paramMap.put("service_passwd", getSha512Pwd(dto.getAppId()));
        paramMap.put("service_key", dto.getRegId());

        dto.getItems().forEach(e -> {
            String[] item = e.split("\\!\\^");
            if (item.length == 2) {
                paramMap.put(item[0], item[1]);
            }
        });


        ReflectionTestUtils.setField(pushSingleSocketClient, "socketMax", "10");
        ReflectionTestUtils.setField(pushSingleSocketClient, "socketMin", "2");
        ReflectionTestUtils.setField(pushSingleSocketClient, "lgSocketMax", "10");
        ReflectionTestUtils.setField(pushSingleSocketClient, "lgSocketMin", "2");

        ReflectionTestUtils.setField(pushSingleSocketClient, "host", "211.115.75.227");
        ReflectionTestUtils.setField(pushSingleSocketClient, "port", "9600");
        ReflectionTestUtils.setField(pushSingleSocketClient, "timeout", "2000");
        ReflectionTestUtils.setField(pushSingleSocketClient, "channelPort", "8080");
        ReflectionTestUtils.setField(pushSingleSocketClient, "defaultChannelHost", "PsAgt");
        ReflectionTestUtils.setField(pushSingleSocketClient, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(pushSingleSocketClient, "closeSecond", "170");
        ReflectionTestUtils.setField(pushSingleSocketClient, "pushSocketInitCnt", "5");

        ReflectionTestUtils.setField(pushSingleSocketClient, "lgHost", "211.115.75.227");
        ReflectionTestUtils.setField(pushSingleSocketClient, "lgPort", "8100");
        ReflectionTestUtils.setField(pushSingleSocketClient, "lgTimeout", "2000");
        ReflectionTestUtils.setField(pushSingleSocketClient, "lgChannelPort", "8080");
        ReflectionTestUtils.setField(pushSingleSocketClient, "lgDefaultChannelHost", "PsAgt");
        ReflectionTestUtils.setField(pushSingleSocketClient, "lgDestinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(pushSingleSocketClient, "lgCloseSecond", "170");
        ReflectionTestUtils.setField(pushSingleSocketClient, "lgPushSocketInitCnt", "5");

        ReflectionTestUtils.setField(pushSingleSocketClient, "lgPushServiceId", "00007");
        ReflectionTestUtils.setField(pushSingleSocketClient, "pushIntervalTime", "1000");

        pushSingleSocketClient.initialize();
    }

    //@Test
    void requestPushSingle() {

        pushSingleSocketClient.getPushStatus("id", 1, 1000);
        pushSingleSocketClient.putPushStatus("id", 1, 1000);
        pushSingleSocketClient.socketClientSch();
        //pushSingleSocketClient.destroy();

        PushResponseDto responseDto = pushSingleSocketClient.requestPushSingle(paramMap);
        log.debug("junit result: " + responseDto.getStatusCode());
        //Assertions.assertTrue("200".equals(responseDto.getStatusCode()));
        Assertions.assertTrue("412".equals(responseDto.getStatusCode()));
    }

    @Test
    void requestPushSingle_exception1() {
        pushSingleSocketClient.destroy();
        PushResponseDto responseDto = pushSingleSocketClient.requestPushSingle(paramMap);
        log.debug("junit result: " + responseDto.getStatusCode());

        Assertions.assertTrue("500".equals(responseDto.getStatusCode()));
    }

   // @Test
    void requestPushSingle_exception2() throws PushBizException {
        PushBizException ex = new PushBizException("");
        given( socketInfo.sendPushNotice(anyMap()) ).willThrow(ex);

        PushResponseDto responseDto = pushSingleSocketClient.requestPushSingle(paramMap);
        log.debug("junit result: " + responseDto.getStatusCode());

        Assertions.assertTrue("503".equals(responseDto.getStatusCode()));
    }
/*
    @Test
    void requestPushSingle_exception3() {
        Exception  ex = new Exception ("");
        given( pushSingleSocketClient.requestPushSingle(anyMap()) ).willThrow(ex);

        PushResponseDto responseDto = pushSingleSocketClient.requestPushSingle(paramMap);
        Assertions.assertTrue("500".equals(responseDto.getStatusCode()));
    }
    */
}