package com.likelion.backendplus4.talkpick.backend.chat.presentation.validation;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.CategoryType;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 채팅 카테고리 형식을 검증하는 Validator 구현체입니다.
 * CategoryType enum을 기반으로 유효한 카테고리인지 검증합니다.
 *
 * @since 2025-06-03
 * @author 양병학
 */
public class CategoryConstraintValidator implements ConstraintValidator<CategoryConstraint, String> {

    /**
     * 카테고리의 유효성을 검증합니다.
     *
     * 1. null 또는 빈 문자열 체크
     * 2. CategoryType enum에 정의된 유효한 카테고리인지 확인
     *
     * @param category 검증할 카테고리
     * @param context 검증 컨텍스트
     * @return 유효하면 true, 아니면 false
     * @since 2025-06-03
     * @author 양병학
     */
    @Override
    public boolean isValid(String category, ConstraintValidatorContext context) {
        if (isNullOrEmpty(category)) {
            return false;
        }

        return CategoryType.isValid(category);
    }

    /**
     * 문자열이 null이거나 비어있는지 확인합니다.
     *
     * 1. null 체크
     * 2. 공백 제거 후 빈 문자열 체크
     *
     * @param value 확인할 문자열
     * @return null이거나 비어있으면 true
     * @since 2025-06-03
     * @author 양병학
     */
    private boolean isNullOrEmpty(String value) {
        return null == value || value.trim().isEmpty();
    }
}