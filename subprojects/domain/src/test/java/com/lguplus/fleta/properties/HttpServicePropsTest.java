package com.lguplus.fleta.properties;

import com.lguplus.fleta.config.HttpPushConfig;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

class HttpServicePropsTest {

    @Test
    void testFindMapByServiceId() {
        HttpServiceProps httpServiceProps = new HttpServiceProps(new HttpPushConfig.HttpPushExceptionCode(), new HttpPushConfig.HttpPushExceptionMessage());

        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("id", "30015");
        keyMap.put("password", "lguplusuflix");

        ReflectionTestUtils.setField(httpServiceProps, "service", List.of(keyMap));

        Map<String, String> rstMap = httpServiceProps.findMapByServiceId("30015").get();

        assertThat(rstMap).containsEntry("password", "lguplusuflix");
    }

    @Test
    void testGetExceptionCodeMessage() {
        HttpPushConfig.HttpPushExceptionCode httpPushExceptionCode = new HttpPushConfig.HttpPushExceptionCode();
        Map<String, String> codeMap = new HashMap<>();
        codeMap.put("ServiceIdNotFoundException", "1115");

        ReflectionTestUtils.setField(httpPushExceptionCode, "httpPush", codeMap);

        HttpPushConfig.HttpPushExceptionMessage httpPushExceptionMessage = new HttpPushConfig.HttpPushExceptionMessage();
        Map<String, String> messageMap = new HashMap<>();
        codeMap.put("1115", "서비스ID 확인 불가");

        ReflectionTestUtils.setField(httpPushExceptionMessage, "message", messageMap);

        HttpServiceProps httpServiceProps = new HttpServiceProps(httpPushExceptionCode, httpPushExceptionMessage);

        Pair<String, String> cdMsgPair = httpServiceProps.getExceptionCodeMessage("ServiceIdNotFoundException");

        assertThat(cdMsgPair.getLeft()).isEqualTo("1115");
    }

}