package com.likelion.backendplus4.talkpick.backend.chat.presentation.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 채팅 카테고리 형식을 검증하는 커스텀 validation 어노테이션입니다.
 * CategoryType enum에 정의된 유효한 카테고리만 허용합니다.
 *
 * @since 2025-06-03
 * @author 양병학
 */
@Documented
@Constraint(validatedBy = CategoryConstraintValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryConstraint {

    /**
     * 검증 실패 시 반환할 기본 오류 메시지입니다.
     *
     * @return 오류 메시지
     */
    String message() default "부적절한 카테고리입니다. 허용된 값: politics, sports, entertainment, economy, society, international";

    /**
     * 검증 그룹을 지정합니다.
     *
     * @return 검증 그룹 배열
     */
    Class<?>[] groups() default {};

    /**
     * 추가 메타데이터를 전달하기 위한 payload입니다.
     *
     * @return payload 배열
     */
    Class<? extends Payload>[] payload() default {};
}