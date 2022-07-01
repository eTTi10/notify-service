package com.lguplus.fleta.properties;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
class SendPushCodePropsTest {

    @InjectMocks
    SendPushCodeProps sendPushCodeProps;

    @Test
    void findMapBySendCode() {

        Map<String, String> sendCodeMap = new HashMap<>();
        sendCodeMap.put("sendcode", "P001");
        sendCodeMap.put("pos.send", "Y");

        ReflectionTestUtils.setField(sendPushCodeProps, "sendCodes", List.of(sendCodeMap));

        Map<String, String> pushInfoMap = sendPushCodeProps.findMapBySendCode("P001").get();
        assertThat(pushInfoMap).containsEntry("pos.send", "Y");
    }

    @Test
    void findMapByServiceType() {

        Map<String, String> serviceTargetMap = new HashMap<>();
        serviceTargetMap.put("serviceTarget", "C");
        serviceTargetMap.put("gcm.appid", "musicshow_gcm");

        ReflectionTestUtils.setField(sendPushCodeProps, "serviceTargets", List.of(serviceTargetMap));
        Map<String, String> appInfoMap = sendPushCodeProps.findMapByServiceType("C").get();

        assertThat(appInfoMap).containsEntry("gcm.appid", "musicshow_gcm");

    }

}