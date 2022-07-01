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
public class DataConsumerAdvice {

    @Around("@annotation(org.springframework.kafka.annotation.KafkaListener)")
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            return joinPoint.proceed();
        } catch (final Throwable e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
