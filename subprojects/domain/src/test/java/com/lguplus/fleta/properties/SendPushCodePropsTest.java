package com.lguplus.fleta.properties;

import com.lguplus.fleta.util.JunitTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
class SendPushCodePropsTest {

    @InjectMocks
    SendPushCodeProps sendPushCodeProps;

    @Test
    void findMapBySendCode() {


        Map<String, String> sendCodeMap = new HashMap<>();
        sendCodeMap.put("sendcode", "P001");
        sendCodeMap.put("pos.send", "Y");

        JunitTestUtils.setValue(sendPushCodeProps, "sendCodes", List.of(sendCodeMap));

        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode("P001").get();
        assertThat(pushInfoMap).containsEntry("pos.send", "Y");
    }

    @Test
    void findMapByServiceType() {

        Map<String, String> serviceTargetMap = new HashMap<>();
        serviceTargetMap.put("serviceTarget", "C");
        serviceTargetMap.put("gcm.appid", "musicshow_gcm");

        JunitTestUtils.setValue(sendPushCodeProps, "serviceTargets", List.of(serviceTargetMap));
        Map<String, String> appInfoMap = sendPushCodeProps.findMapByServiceType("C").get();

        assertThat(appInfoMap).containsEntry("gcm.appid", "musicshow_gcm");

    }

}