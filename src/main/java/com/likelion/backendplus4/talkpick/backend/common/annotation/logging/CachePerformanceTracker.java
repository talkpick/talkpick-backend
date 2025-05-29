package com.likelion.backendplus4.talkpick.backend.common.annotation.logging;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 캐시 성능 측정 및 프로메테우스 메트릭 전송을 위한 애노테이션
 *
 * 로그 출력은 하지 않고 메트릭 전송만 담당
 * TimeTracker와 함께 사용하여 로그와 메트릭을 분리
 *
 * @since 2025-05-28
 * @author 양병학
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CachePerformanceTracker {
}