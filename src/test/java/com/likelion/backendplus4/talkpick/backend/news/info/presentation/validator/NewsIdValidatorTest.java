package com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * NewsIdValidator의 단위 테스트 클래스입니다.
 * 뉴스 ID 검증 로직의 정확성을 확인합니다.
 *
 * @since 2025-05-25
 */
@DisplayName("뉴스 ID Validator 테스트")
class NewsIdValidatorTest {

	private NewsIdValidator validator;

	/**
	 * 각 테스트 실행 전 validator 초기화를 수행합니다.
	 */
	@BeforeEach
	void setUp() {
		validator = new NewsIdValidator();
		validator.initialize(null); // 초기화
	}

	/**
	 * 유효한 뉴스 ID 형식에 대한 검증 성공 테스트입니다.
	 *
	 * @param validNewsId 유효한 뉴스 ID
	 */
	@DisplayName("유효한 뉴스 ID 검증 성공")
	@ParameterizedTest
	@ValueSource(strings = {"KM123", "DA456", "KH789", "KM1", "DA999999"})
	void 유효한_뉴스ID_검증_성공(String validNewsId) {
		boolean result = validator.isValid(validNewsId, null);

		assertThat(result).isTrue();
	}

	/**
	 * 잘못된 형식의 뉴스 ID에 대한 검증 실패 테스트입니다.
	 *
	 * @param invalidNewsId 잘못된 형식의 뉴스 ID
	 */
	@DisplayName("잘못된 형식의 뉴스 ID 검증 실패")
	@ParameterizedTest
	@ValueSource(strings = {
		"km123",      // 소문자 접두사
		"K123",       // 접두사 1글자
		"KMA123",     // 접두사 3글자
		"AB123",      // 유효하지 않은 접두사
		"KM",         // 숫자 부분 누락
		"123KM",      // 순서 잘못
		"KM12A",      // 숫자 부분에 문자 포함
		"",           // 빈 문자열
		"   ",        // 공백만 있는 문자열
		"test"        // 완전히 다른 형식
	})
	void 잘못된_형식의_뉴스ID_검증_실패(String invalidNewsId) {
		boolean result = validator.isValid(invalidNewsId, null);

		assertThat(result).isFalse();
	}

	/**
	 * null 뉴스 ID에 대한 검증 실패 테스트입니다.
	 */
	@Test
	@DisplayName("null 뉴스 ID 검증 실패")
	void null_뉴스ID_검증_실패() {
		boolean result = validator.isValid(null, null);

		assertThat(result).isFalse();
	}
}