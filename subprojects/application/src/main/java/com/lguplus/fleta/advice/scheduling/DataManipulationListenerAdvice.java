package com.lguplus.fleta.advice.scheduling;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class DataManipulationListenerAdvice {

    @Around("target(com.lguplus.fleta.service.DataManipulationListener) && (" +
        "execution(public void *.onInsert(java.util.Map)) || " +
        "execution(public void *.onUpdate(java.util.Map, java.util.Map)) || " +
        "execution(public void *.onDelete(java.util.Map))" +
        ")")
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            return joinPoint.proceed();
        } catch (final Throwable e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
