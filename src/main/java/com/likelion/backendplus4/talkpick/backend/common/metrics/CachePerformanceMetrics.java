package com.likelion.backendplus4.talkpick.backend.common.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * 캐시 성능 메트릭을 프로메테우스로 전송하는 클래스입니다.
 *
 * 메서드 실행 시간을 측정하여 프로메테우스 메트릭으로 수집합니다.
 *
 * @author 양병학
 * @since 2025-05-28 최초 작성
 */
@Component
@RequiredArgsConstructor
public class CachePerformanceMetrics {

    private final MeterRegistry meterRegistry;

    /**
     * 캐시 성능 메트릭을 프로메테우스로 전송합니다.
     *
     * 1. 클래스명과 메서드명을 추출하여 태그로 설정
     * 2. 계층 정보를 판단하여 태그에 추가
     * 3. 실행 시간을 프로메테우스 타이머로 기록
     *
     * @param pjp 실행 중인 JoinPoint
     * @param elapsedSeconds 실행 시간 (초)
     * @author 양병학
     * @since 2025-05-28 최초 작성
     */
    public void recordCachePerformance(ProceedingJoinPoint pjp, double elapsedSeconds) {
        String className = extractClassName(pjp);
        String methodName = extractMethodName(pjp);
        String layer = determineLayer(className);

        recordToPrometheus(className, methodName, layer, elapsedSeconds);
    }

    /**
     * JoinPoint에서 클래스명을 추출합니다.
     *
     * @param pjp 실행 중인 JoinPoint
     * @return 클래스명
     */
    private String extractClassName(ProceedingJoinPoint pjp) {
        return pjp.getTarget().getClass().getSimpleName();
    }

    /**
     * JoinPoint에서 메서드명을 추출합니다.
     *
     * @param pjp 실행 중인 JoinPoint
     * @return 메서드명
     */
    private String extractMethodName(ProceedingJoinPoint pjp) {
        return pjp.getSignature().getName();
    }

    /**
     * 클래스명을 기반으로 계층 정보를 판단합니다.
     *
     * @param className 판단할 클래스명
     * @return 계층 정보 ("cache", "business", "infrastructure", "unknown")
     */
    private String determineLayer(String className) {
        if (className.contains("CacheService")) {
            return "cache";
        }
        if (className.contains("NewsService")) {
            return "business";
        }
        if (className.contains("Adapter") || className.contains("Repository")) {
            return "infrastructure";
        }
        return "unknown";
    }

    /**
     * 프로메테우스에 메트릭을 기록합니다.
     *
     * @param className 클래스명
     * @param methodName 메서드명
     * @param layer 계층 정보
     * @param elapsedSeconds 실행 시간 (초)
     */
    private void recordToPrometheus(String className, String methodName, String layer, double elapsedSeconds) {
        Timer.builder("cache_performance_seconds")
                .description("캐시 성능 측정 (초)")
                .tag("class", className)
                .tag("method", methodName)
                .tag("layer", layer)
                .register(meterRegistry)
                .record(Duration.ofMillis((long)(elapsedSeconds * 1000)));
    }
}