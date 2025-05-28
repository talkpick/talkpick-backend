package com.likelion.backendplus4.talkpick.backend.common.aop.logging;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.CachePerformanceTracker;
import com.likelion.backendplus4.talkpick.backend.common.metrics.CachePerformanceMetrics;

import lombok.RequiredArgsConstructor;

/**
 * CachePerformanceTracker 애노테이션이 적용된 메서드의 성능을 측정하여 메트릭으로 전송하는 AOP 클래스
 *
 * 로그 출력은 하지 않고 메트릭 전송만 담당 (기존 TimeTracker가 로그 담당)
 *
 * @since 2025-05-28
 * @author 양병학
 */
@Aspect
@Component
@Order(3)
@RequiredArgsConstructor
public class CachePerformanceTrackerAspect {

    private final CachePerformanceMetrics metricsCollector;

    /**
     * CachePerformanceTracker 애노테이션이 적용된 메서드를 감싸서 성능 메트릭을 수집한다.
     */
    @Around("@annotation(tracker)")
    public Object trackCachePerformance(ProceedingJoinPoint pjp, CachePerformanceTracker tracker) throws Throwable {
        long start = System.currentTimeMillis();

        try {
            Object result = pjp.proceed();

            // 메트릭 전송만 수행 (로그 출력 없음)
            double elapsedSeconds = (System.currentTimeMillis() - start) / 1000.0;
            metricsCollector.recordCachePerformance(pjp, elapsedSeconds);

            return result;

        } catch (Throwable throwable) {
            double elapsedSeconds = (System.currentTimeMillis() - start) / 1000.0;
            metricsCollector.recordCachePerformance(pjp, elapsedSeconds);

            throw throwable;
        }
    }
}