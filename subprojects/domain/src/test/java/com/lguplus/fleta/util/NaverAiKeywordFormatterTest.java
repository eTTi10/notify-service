package com.lguplus.fleta.util;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class NaverAiKeywordFormatterTest {

    @Test
    void testFormat() {

        final String keyword = "뿡뿡! 괴도와 납치된 신부 사건(상)(우리말)";
        final String expect = "뿡뿡! 괴도와 납치된 신부 사건";
        final String result = NaverAiKeywordFormatter.format(keyword);
        Assertions.assertThat(result).isEqualTo(expect);
    }

    @Test
    void testNoActionIfNull() {

        final String result = NaverAiKeywordFormatter.format(null);
        Assertions.assertThat(result).isNull();
    }
}
