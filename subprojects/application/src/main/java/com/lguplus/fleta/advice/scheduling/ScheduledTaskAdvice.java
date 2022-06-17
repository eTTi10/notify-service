package com.lguplus.fleta.advice.scheduling;

import com.lguplus.fleta.data.annotation.SynchronousScheduled;
import com.lguplus.fleta.service.ManipulationDataDispatcher;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RPermitExpirableSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Profile("!test")
@Slf4j
@Aspect
@Component
@AllArgsConstructor
public class ScheduledTaskAdvice {

    private final RedissonClient redissonClient;

    @Around("@annotation(com.lguplus.fleta.data.annotation.SynchronousScheduled)")
    public Object proceed(final ProceedingJoinPoint joinPoint) throws Throwable {

        final SynchronousScheduled annotation = getAnnotation(joinPoint.getSignature());
        if (annotation == null || ManipulationDataDispatcher.WORKER_NAME.equals(Thread.currentThread().getName())) {
            return proceedInternal(joinPoint);
        }

        final RPermitExpirableSemaphore semaphore =
                redissonClient.getPermitExpirableSemaphore("CN::SEMAPHORE::" + annotation.semaphore());
        if (!semaphore.isExists() && !semaphore.trySetPermits(1)) {
            return null;
        }

        final String permitId =  semaphore.tryAcquire();
        if (permitId == null) {
            return null;
        }

        semaphore.updateLeaseTime(permitId, annotation.autoReleaseSeconds(), TimeUnit.SECONDS);
        try {
            return proceedInternal(joinPoint);
        } finally {
            semaphore.release(permitId);
        }
    }

    private SynchronousScheduled getAnnotation(final Signature signature) {

        if (!(signature instanceof MethodSignature)) {
            return null;
        }

        final Method method = ((MethodSignature)signature).getMethod();
        if (method.getReturnType() != void.class) {
            if (log.isWarnEnabled()) {
                log.warn("SynchronousScheduled method should returns void.");
            }
            return null;
        }

        return method.getAnnotation(SynchronousScheduled.class);
    }

    private Object proceedInternal(final ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            return joinPoint.proceed();
        } catch (final Throwable e) {
            log.error(e.getMessage(), e);
            throw e;
        }
    }
}
