package com.lguplus.fleta.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class LogInterceptorTest {

    LogInterceptor logInterceptor = new LogInterceptor();

    @Test
    @DisplayName("로깅 대상 Header 항목이 null인 경우에도 NPE가 발생하지 않아야 한다.")
    void preHandle() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        boolean result = logInterceptor.preHandle(request, response, "");
        assertThat(result).isTrue();
    }
}