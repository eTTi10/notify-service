package com.lguplus.fleta.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * 공통 HTTP 응답시간 측정 Interceptor
 * @version 1.1
 */
public class MetricInterceptor implements HandlerInterceptor {

    public static final String START_TIME = "startTime";
    public static final String MID_TIME = "midTime";
    public static final String RESPONSE_TIME = "responseTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME, startTime);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long midTime = System.currentTimeMillis();
        request.setAttribute(MID_TIME, midTime);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        Long endTime = System.currentTimeMillis();
        Long startTime = (Long) request.getAttribute(START_TIME);
        Long responseTime = endTime - startTime;
        request.setAttribute(RESPONSE_TIME, responseTime);
    }
}
