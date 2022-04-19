package com.lguplus.fleta.provider.socket;

import com.lguplus.fleta.data.dto.PushStatDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestItemDto;
import com.lguplus.fleta.data.dto.request.inner.PushRequestSingleDto;
import com.lguplus.fleta.data.dto.response.inner.PushResponseDto;
import com.lguplus.fleta.provider.socket.multi.NettyTcpJunitServerTest;
import com.lguplus.fleta.provider.socket.pool.PushSocketInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.junit.jupiter.api.*;
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
@TestMethodOrder(MethodOrderer.MethodName.class)
class PushSingleSocketClientImplTest {

    static NettyTcpJunitServerTest server;
    static String SERVER_IP = "127.0.0.1";
    static int SERVER_PORT = 9600;

    private final PushSingleSocketClientImpl pushSingleSocketClientImpl;

    PushRequestSingleDto pushRequestSingleDto, pushRequestSingleDtoLg;

    Map<String, String> paramMap;
    Map<String, String> paramMapLg;

    final String sendSuccessCode = "200"; //200

    @BeforeAll
    static void setUpAll() throws InterruptedException {
        server = new NettyTcpJunitServerTest();
        new Thread(() -> {
            server.runServer(SERVER_PORT);
        }).start();
        Thread.sleep(200);
    }

    @AfterAll
    static void setUpClose() {
        server.stopServer();
    }

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
    void setUp() throws InterruptedException {

        List<PushRequestItemDto> addItems = new ArrayList<>();
        addItems.add(PushRequestItemDto.builder().itemKey("badge").itemValue("1").build());
        addItems.add(PushRequestItemDto.builder().itemKey("sound").itemValue("ring.caf").build());
        addItems.add(PushRequestItemDto.builder().itemKey("cm").itemValue("aaaa").build());

        pushRequestSingleDto = PushRequestSingleDto.builder()
                .serviceId("30011")
                .pushType("G")
                .applicationId("lguplushdtvgcm")
                .regId("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=")
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();

        PushRequestSingleDto dto = pushRequestSingleDto;

        paramMap = new HashMap<>();
        paramMap.put("msg_id", "PUSH_NOTI");
        paramMap.put("push_id", "202112200001");
        paramMap.put("service_id", dto.getServiceId());
        paramMap.put("app_id", dto.getApplicationId());
        paramMap.put("noti_contents", dto.getMessage());
        paramMap.put("service_passwd", getSha512Pwd(dto.getApplicationId()));
        paramMap.put("service_key", dto.getRegId());

        dto.getItems().forEach(e -> paramMap.put(e.getItemKey(), e.getItemValue()));

        /////////////// LG
        pushRequestSingleDtoLg = PushRequestSingleDto.builder()
                .serviceId("00007")
                .pushType("G")
                .applicationId("smartux")
                .regId("MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=")
                .message("\"PushCtrl\":\"ON\",\"MESSGAGE\": \"NONE\"")
                .items(addItems)
                .build();

        dto = pushRequestSingleDtoLg;

        paramMapLg = new HashMap<>();
        paramMapLg.put("msg_id", "PUSH_NOTI");
        paramMapLg.put("push_id", "202112200101");
        paramMapLg.put("service_id", dto.getServiceId());
        paramMapLg.put("app_id", dto.getApplicationId());
        paramMapLg.put("noti_contents", dto.getMessage());
        paramMapLg.put("service_passwd", getSha512Pwd(dto.getApplicationId()));
        paramMapLg.put("push_app_id", "smartux0001");
        paramMapLg.put("noti_type", "POS");
        paramMapLg.put("regist_id", dto.getRegId());

        dto.getItems().forEach(e -> paramMap.put(e.getItemKey(), e.getItemValue()));

        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "socketMax", "2");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "socketMin", "1");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgSocketMax", "2");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgSocketMin", "1");

        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "host", SERVER_IP);
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "port", "" + SERVER_PORT);
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "timeout", "2000");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "wasPort", "8080");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "defaultChannelHost", "PsAgt");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "destinationIp", "222.231.13.85");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "closeSecond", "170");
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "pushSocketInitCnt", "5");

        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgHost", SERVER_IP);
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgPort", "" + SERVER_PORT);
        ReflectionTestUtils.setField(pushSingleSocketClientImpl, "lgTimeout", "2000");
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
        try {
            ReflectionTestUtils.invokeMethod(pushSingleSocketClientImpl, "destroy");
        }
        catch (Exception e) {

        }
    }

    //ok
    @Test // Push
    //@Disabled
    void test01_requestPushSingle_case_01() {

        PushResponseDto responseDto = pushSingleSocketClientImpl.requestPushSingle(paramMap);
        log.debug("junit result: " + responseDto.getStatusCode());
        pushSingleSocketClientImpl.socketClientSch();

        Assertions.assertEquals(sendSuccessCode, responseDto.getStatusCode());

    }

    //ok
    @Test() // Push Lg
    //@Disabled
    void test02_requestPushSingle_case_02() {

        PushResponseDto responseDto = pushSingleSocketClientImpl.requestPushSingle(paramMapLg);
        log.debug("junit result: " + responseDto.getStatusCode());

        Assertions.assertEquals(sendSuccessCode, responseDto.getStatusCode());
    }

    @Test // pool empty Exception
    void test03_requestPushSingle_case_03()  {

        Long currentTimeMillis = System.currentTimeMillis();
        Long pushCount = 10L;

        pushSingleSocketClientImpl.putPushStatus("00007", pushCount, currentTimeMillis);
        PushStatDto pushStatDto = pushSingleSocketClientImpl.getPushStatus("00007", pushCount, currentTimeMillis);

        Assertions.assertEquals(currentTimeMillis, pushStatDto.getMeasureStartMillis());
        Assertions.assertEquals(pushCount, pushStatDto.getMeasurePushCount());

    }

    @Test // pool empty Exception
    void test04_requestPushSingle_case_04()  {

        int EXTRA_CONN_COUNT = 50;
        List<GenericObjectPool<PushSocketInfo>> poolListEmpty = (List<GenericObjectPool<PushSocketInfo>>)ReflectionTestUtils.getField(pushSingleSocketClientImpl, "socketPools");

        for(int i=0; i<2+EXTRA_CONN_COUNT; i++) {
            try {
                PushSocketInfo pushSocketInfo = poolListEmpty.get(0).borrowObject();
                PushSocketInfo pushSocketInfo1 = poolListEmpty.get(1).borrowObject();
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