package com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * 뉴스 ID 형식을 검증하는 커스텀 validation 어노테이션입니다.
 * 유효한 접두사(KM, DA, KH)로 시작하고 숫자가 뒤따르는 형식만 허용합니다.
 *
 * @since 2025-05-25
 * @author 양병학
 */
@Documented
@Constraint(validatedBy = NewsIdValidator.class)
@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NewsIdConstraint {

	/**
	 * 검증 실패 시 반환할 기본 오류 메시지입니다.
	 *
	 * @return 오류 메시지
	 */
	String message() default "뉴스 ID는 유효한 형식이어야 합니다 (예: KM123, DA456, KH789)";

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