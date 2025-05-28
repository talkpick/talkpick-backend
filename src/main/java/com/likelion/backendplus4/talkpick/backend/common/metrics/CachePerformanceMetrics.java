package com.likelion.backendplus4.talkpick.backend.common.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 캐시 성능 메트릭을 프로메테우스로 전송하는 클래스
 *
 * @since 2025-05-28
 * @author 양병학
 */
@Component
@RequiredArgsConstructor
public class CachePerformanceMetrics {

    private final MeterRegistry meterRegistry;

    /**
     * 캐시 성능 메트릭을 프로메테우스로 전송한다.
     *
     * @param pjp 실행 중인 JoinPoint
     * @param elapsedSeconds 실행 시간 (초)
     */
    public void recordCachePerformance(ProceedingJoinPoint pjp, double elapsedSeconds) {
        String className = pjp.getTarget().getClass().getSimpleName();
        String methodName = pjp.getSignature().getName();
        String layer = determineLayer(className);

        Timer.builder("cache_performance_seconds")
                .description("캐시 성능 측정 (초)")
                .tag("class", className)
                .tag("method", methodName)
                .tag("layer", layer)
                .register(meterRegistry)
                .record(Duration.ofMillis((long)(elapsedSeconds * 1000)));
    }

    private String determineLayer(String className) {
        if (className.contains("CacheService")) {
            return "cache";
        } else if (className.contains("NewsService")) {
            return "business";
        } else {
            return "unknown";
        }
    }
}
