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
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class SmsAgentPropsTest {

    @InjectMocks
    SmsAgentProps smsAgentProps;

    @Test
    void findMapByIndex() {

        Map<String, String> serverMap = new HashMap<>();
        serverMap.put("index","1");
        serverMap.put("id","test");
        serverMap.put("password","test");

        JunitTestUtils.setValue(smsAgentProps, "servers", List.of(serverMap));
        Map<String, String> serverInfoMap = smsAgentProps.findMapByIndex("1").get();
        assertThat(serverInfoMap.get("id").equals(serverMap.get("id")));
    }
}