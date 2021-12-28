package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.provider.socket.pool.PushSocketInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@ExtendWith({ MockitoExtension.class})
class PushSingleSocketClientImplTest {

    private final PushSingleSocketClientImpl pushSingleSocketClientImpl;

    PushRequestSingleDto pushRequestSingleDto, pushRequestSingleDtoLg;

    Map<String, String> paramMap;
    Map<String, String> paramMapLg;

    List<GenericObjectPool<PushSocketInfo>> poolList;

    final String sendSuccessCode = "412"; //200

    public PushSingleSocketClientImplTest() {
        pushSingleSocketClientImpl = new PushSingleSocketClientImpl();
    }

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

        /////////////// LG
        pushRequestSingleDtoLg = PushRequestSingleDto.builder()
                .serviceId("00007")
                .pushType("G")
                .appId("smartux")
                .regId("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=")
                .msg("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(items)
                .build();

        dto = pushRequestSingleDtoLg;

        paramMapLg = new HashMap<>();
        paramMapLg.put("msg_id", "PUSH_NOTI");
        paramMapLg.put("push_id", "202112200101");
        paramMapLg.put("service_id", dto.getServiceId());
        paramMapLg.put("app_id", dto.getAppId());
        paramMapLg.put("noti_contents", dto.getMsg());
        paramMapLg.put("service_passwd", getSha512Pwd(dto.getAppId()));
        paramMapLg.put("push_app_id", "smartux0001");
        paramMapLg.put("noti_type", "POS");
        paramMapLg.put("regist_id", dto.getRegId());

        dto.getItems().forEach(e -> {
            String[] item = e.split("\\!\\^");
            if (item.length == 2) {
                paramMap.put(item[0], item[1]);
            }
        });

        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "socketMax", "2");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "socketMin", "1");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgSocketMax", "2");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgSocketMin", "1");

        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "host", "211.115.75.227");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "port", "9600");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "timeout", "2000");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "channelPort", "8080");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "defaultChannelHost", "PsAgt");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "closeSecond", "170");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "pushSocketInitCnt", "5");

        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgHost", "211.115.75.227");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgPort", "8100");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgTimeout", "2000");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgChannelPort", "8080");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgDefaultChannelHost", "PsAgt");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgDestinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgCloseSecond", "170");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgPushSocketInitCnt", "5");

        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgPushServiceId", "00007");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "pushIntervalTime", "1");

        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "measureIntervalMillis", 1000);

        ReflectionTestUtils.invokeMethod(pushSingleSocketClientImpl, "initialize");
    }

    private void clearPool() {
        //ReflectionTestUtils.invokeMethod(pushSingleSocketClient, "socketClientSch");
        ReflectionTestUtils.invokeMethod(pushSingleSocketClientImpl, "destroy");
    }

    //ok
    @Test // Push
    //@Disabled
    void requestPushSingle_case_01() {

        PushResponseDto responseDto = pushSingleSocketClientImpl.requestPushSingle(paramMap);
        log.debug("junit result: " + responseDto.getStatusCode());

        Assertions.assertEquals(sendSuccessCode, responseDto.getStatusCode());

    }

    //ok
    @Test() // Push Lg
    //@Disabled
    void requestPushSingle_case_02() {

        PushResponseDto responseDto = pushSingleSocketClientImpl.requestPushSingle(paramMapLg);
        log.debug("junit result: " + responseDto.getStatusCode());

        Assertions.assertEquals(sendSuccessCode, responseDto.getStatusCode());
    }

    @Test // pool empty Exception
    void requestPushSingle_case_03()  {

        Long currentTimeMillis = System.currentTimeMillis();
        Long pushCount = 10L;

        pushSingleSocketClientImpl.putPushStatus("00007", pushCount, currentTimeMillis);
        PushStatDto pushStatDto = pushSingleSocketClientImpl.getPushStatus("00007", pushCount, currentTimeMillis);

        Assertions.assertEquals(currentTimeMillis, pushStatDto.getMeasureStartMillis());
        Assertions.assertEquals(pushCount, pushStatDto.getMeasurePushCount());

    }

    @Test // pool empty Exception
    void requestPushSingle_case_04()  {

        List<GenericObjectPool<PushSocketInfo>> poolListEmpty = (List<GenericObjectPool<PushSocketInfo>>)ReflectionTestUtils.getField(pushSingleSocketClientImpl, "poolList");

        for(int i=0; i<2; i++) {
            try {
                PushSocketInfo pushSocketInfo = poolListEmpty.get(0).borrowObject();
                //poolListEmpty.get(0).returnObject(pushSocketInfo);
                PushSocketInfo pushSocketInfo1 = poolListEmpty.get(1).borrowObject();
                //poolListEmpty.get(1).returnObject(pushSocketInfo1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        PushResponseDto responseDto = pushSingleSocketClientImpl.requestPushSingle(paramMap);
        log.debug("junit result: " + responseDto.getStatusCode());

        Assertions.assertEquals("500", responseDto.getStatusCode());

    }

    @AfterEach
    void after() {
        clearPool();
    }

}