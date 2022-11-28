package com.lguplus.fleta.interceptor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 공통 HTTP 요청/응답 로깅 Interceptor
 * @version 1.1
 */
@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String RESPONSE_TIME = "responseTime";
    public static final List<String> LOG_HEADER_NAMES = List.of(HttpHeaders.ACCEPT, HttpHeaders.USER_AGENT);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        putCommonDataToMdc(request);
        logRequestBasicInfo(request);
        logRequestHeaderInfo(request);
        return true;
    }

    private void putCommonDataToMdc(HttpServletRequest request) {
        MDC.put("saId", getSaIdParameter(request));
        MDC.put("stbMac", getStbMacParameter(request));
    }

    private String getSaIdParameter(HttpServletRequest request) {
        String saId = request.getParameter("sa_id");
        if (StringUtils.hasText(saId)) {
            return saId;
        }
        saId = request.getParameter("SA_ID");
        if (StringUtils.hasText(saId)) {
            return saId;
        }
        return request.getParameter("saId");
    }

    private String getStbMacParameter(HttpServletRequest request) {
        String stbMac = request.getParameter("stb_mac");
        if (StringUtils.hasText(stbMac)) {
            return stbMac;
        }
        stbMac = request.getParameter("STB_MAC");
        if (StringUtils.hasText(stbMac)) {
            return stbMac;
        }
        return request.getParameter("stbMac");
    }

    private void logRequestBasicInfo(HttpServletRequest request) {
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        String queryString = request.getQueryString();
        log.info("[{}][{}][{}] Request", method, requestUri, queryString);
    }

    private void logRequestHeaderInfo(HttpServletRequest request) {
        if (!LOG_HEADER_NAMES.isEmpty()) {
            Map<String, String> headerNameValueMap = LOG_HEADER_NAMES.stream()
                .collect(LinkedHashMap::new, (map, name) -> map.put(name, request.getHeader(name)), LinkedHashMap::putAll);
            log.info("[Request Headers] {}", headerNameValueMap);
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String method = request.getMethod();
        String requestUri = request.getRequestURI();
        Long responseTime = (Long) request.getAttribute(RESPONSE_TIME);
        log.info("[{}][{}] Response {}ms", method, requestUri, responseTime);
        MDC.clear();
    }
}
