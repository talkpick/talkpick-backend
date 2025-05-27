package com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator;

import java.util.regex.Pattern;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.enums.NewsIdPrefix;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 뉴스 ID 형식을 검증하는 Validator 구현체입니다.
 * NewsIdPrefix enum을 기반으로
 * 유효한 접두사(ex: KH)와 숫자(ex:11324353) 조합을 검증합니다.
 *
 * @since 2025-05-25
 */
public class NewsIdValidator implements ConstraintValidator<NewsIdConstraint, String> {

	/**
	 * 뉴스 ID 패턴 정규식 (대문자 2글자 + 숫자)
	 */
	private static final Pattern NEWS_ID_PATTERN = Pattern.compile("^([A-Z]{2})(\\d+)$");

	/**
	 * Validator 초기화 메서드입니다.
	 *
	 * @param constraintAnnotation 제약 조건 어노테이션
	 * @since 2025-05-25
	 * @author 양병학
	 */
	@Override
	public void initialize(NewsIdConstraint constraintAnnotation) {
		// 초기화 로직 (필요시 추가)
	}

	/**
	 * 뉴스 ID의 유효성을 검증합니다.
	 *
	 * 1. null 또는 빈 문자열 체크
	 * 2. 패턴 매칭 (접두사 2글자 + 숫자)
	 * 3. 접두사가 유효한 언론사 코드인지 확인
	 *
	 * @param newsId 검증할 뉴스 ID
	 * @param context 검증 컨텍스트
	 * @return 유효하면 true, 아니면 false
	 * @since 2025-05-25
	 * @author 양병학
	 */
	@Override
	public boolean isValid(String newsId, ConstraintValidatorContext context) {
		if (isNullOrEmpty(newsId)) {
			return false;
		}

		if (!isValidPattern(newsId)) {
			return false;
		}

		String prefix = extractPrefix(newsId);
		return NewsIdPrefix.isValidPrefix(prefix);
	}

	/**
	 * 문자열이 null이거나 비어있는지 확인합니다.
	 *
	 * 1. null 체크
	 * 2. 공백 제거 후 빈 문자열 체크
	 *
	 * @param value 확인할 문자열
	 * @return null이거나 비어있으면 true
	 * @since 2025-05-25
	 * @author 양병학
	 */
	private boolean isNullOrEmpty(String value) {
		return value == null || value.trim().isEmpty();
	}

	/**
	 * 뉴스 ID가 올바른 패턴(접두사 + 숫자)인지 확인합니다.
	 *
	 * 1. 정규식 패턴과 매칭 확인
	 * 2. 대문자 2글자 + 숫자 조합 여부 검증
	 *
	 * @param newsId 확인할 뉴스 ID
	 * @return 패턴이 맞으면 true
	 * @since 2025-05-25
	 * @author 양병학
	 */
	private boolean isValidPattern(String newsId) {
		return NEWS_ID_PATTERN.matcher(newsId).matches();
	}

	/**
	 * 뉴스 ID에서 접두사를 추출합니다.
	 *
	 * 1. 문자열 앞 2글자를 접두사로 추출
	 * 2. 언론사 코드 검증을 위해 사용
	 *
	 * @param newsId 뉴스 ID
	 * @return 추출된 접두사 (예: "KM123" → "KM")
	 * @since 2025-05-25
	 * @author 양병학
	 */
	private String extractPrefix(String newsId) {
		return newsId.substring(0, 2);
	}
}