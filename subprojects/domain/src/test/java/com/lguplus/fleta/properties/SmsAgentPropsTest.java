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
class SmsAgentPropsTest {

    @InjectMocks
    SmsAgentProps smsAgentProps;

    @Test
    void findMapByIndex() {

        Map<String, String> serverMap = new HashMap<>();
        serverMap.put("index", "1");
        serverMap.put("id", "test");
        serverMap.put("password", "test");

        ReflectionTestUtils.setField(smsAgentProps, "servers", List.of(serverMap));
        Map<String, String> serverInfoMap = smsAgentProps.findMapByIndex("1").get();
        assertThat(serverInfoMap).containsEntry("id", serverMap.get("id"));
    }
}