package com.lguplus.fleta.properties;

import com.lguplus.fleta.util.JunitTestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class HttpServicePropsTest {

    @Test
    void tFindMapByServiceId() {
        HttpServiceProps httpServiceProps = new HttpServiceProps();

        Map<String, String> keyMap = new HashMap<>();
        keyMap.put("service_id", "30015");
        keyMap.put("service_pwd", "lguplusuflix");

        JunitTestUtils.setValue(httpServiceProps, "keys", List.of(keyMap));

        Map<String, String> rstMap = httpServiceProps.findMapByServiceId("30015").get();

        assertThat(rstMap.get("service_pwd")).isEqualTo("lguplusuflix");
    }

}