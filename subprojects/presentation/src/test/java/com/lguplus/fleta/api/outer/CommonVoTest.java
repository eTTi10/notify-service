package com.lguplus.fleta.api.outer;

import com.lguplus.fleta.data.vo.CommonVo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class CommonVoTest {

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getId() {
        final CommonVo memberVo = CommonVo.builder().saId("saId").appName("appName").build();
        final String saId = memberVo.getSaId();
        assertEquals("saId", saId);
    }
}