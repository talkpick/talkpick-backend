package com.likelion.backendplus4.talkpick.backend.common.aop.logging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.CachePerformanceTracker;
import com.likelion.backendplus4.talkpick.backend.common.metrics.CachePerformanceMetrics;

import lombok.RequiredArgsConstructor;

/**
 * CachePerformanceTracker 애노테이션이 적용된 메서드의 성능을 측정하는 AOP 클래스입니다.
 *
 * 메서드 실행 시간을 측정하고 메트릭 수집기로 위임하여 성능 데이터를 기록합니다.
 * 로그 출력은 하지 않고 메트릭 수집만 담당합니다. (기존 TimeTracker가 로그 담당)
 *
 * @author 양병학
 * @since 2025-05-28 최초 작성
 * @modify 2025-05-29 양병학
 *  - 관심사 분리를 위해 private 메서드로 역할 분담
 *  - public 메서드에서 try-catch 제거
 */
@Slf4j
@Aspect
@Component
@Order(3)
@RequiredArgsConstructor
public class CachePerformanceTrackerAspect {

    private final CachePerformanceMetrics metricsCollector;

    /**
     * CachePerformanceTracker 애노테이션이 적용된 메서드를 감싸서 성능 메트릭을 수집합니다.
     *
     * 1. 메서드 실행 시간 측정
     * 2. 메트릭 수집기로 성능 데이터 전달
     * 3. 원본 메서드 결과 반환
     *
     * @param pjp 실행 중인 JoinPoint
     * @param tracker CachePerformanceTracker 애노테이션
     * @return 메서드 실행 결과
     * @throws Throwable 메서드 실행 중 발생한 예외
     * @author 양병학
     * @since 2025-05-28 최초 작성
     */
    @Around("@annotation(tracker)")
    public Object trackCachePerformance(ProceedingJoinPoint pjp, CachePerformanceTracker tracker) throws Throwable {
        long startTime = measureStartTime();

        Object result = pjp.proceed();

        double elapsedSeconds = calculateElapsedTime(startTime);
        recordPerformanceMetrics(pjp, elapsedSeconds);

        return result;
    }

    /**
     * 시작 시간을 측정합니다.
     *
     * @return 시작 시간 (밀리초)
     */
    private long measureStartTime() {
        return System.currentTimeMillis();
    }

    /**
     * 경과 시간을 계산합니다.
     *
     * @param startTime 시작 시간 (밀리초)
     * @return 경과 시간 (초)
     */
    private double calculateElapsedTime(long startTime) {
        long endTime = System.currentTimeMillis();
        return (endTime - startTime) / 1000.0;
    }

    /**
     * 성능 메트릭을 기록합니다.
     *
     * 메트릭 기록 실패가 비즈니스 로직에 영향을 주지 않도록 예외를 처리합니다.
     *
     * @param pjp 실행 중인 JoinPoint
     * @param elapsedSeconds 경과 시간 (초)
     */
    private void recordPerformanceMetrics(ProceedingJoinPoint pjp, double elapsedSeconds) {
        try {
            metricsCollector.recordCachePerformance(pjp, elapsedSeconds);
        } catch (Exception e) {
            log.debug("메트릭 수집 중 오류가 발생했습니다");
        }
    }
}