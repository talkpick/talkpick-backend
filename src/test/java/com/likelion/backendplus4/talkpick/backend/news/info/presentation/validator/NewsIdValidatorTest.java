package com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NewsIdValidator의 단위 테스트 클래스
 *
 * @since 2025-05-25
 * @author 양병학
 */
@DisplayName("뉴스 ID Validator 테스트")
class NewsIdValidatorTest {

	private NewsIdValidator validator;

	@BeforeEach
	void setUp() {
		validator = new NewsIdValidator();
		validator.initialize(null); // 초기화
	}

	@DisplayName("유효한 뉴스 ID 검증 성공")
	@ParameterizedTest
	@ValueSource(strings = {"KM123", "DA456", "KH789", "KM1", "DA999999"})
	void 유효한_뉴스ID_검증_성공(String validNewsId) {
		boolean result = validator.isValid(validNewsId, null);

		assertThat(result).isTrue();
	}

	@DisplayName("잘못된 형식의 뉴스 ID 검증 실패")
	@ParameterizedTest
	@ValueSource(strings = {
		"km123",
		"K123",
		"KMA123",
		"AB123",
		"KM",
		"123KM",
		"KM12A",
		"",
		"   ",
		"test"
	})
	void 잘못된_형식의_뉴스ID_검증_실패(String invalidNewsId) {
		boolean result = validator.isValid(invalidNewsId, null);

		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("null 뉴스 ID 검증 실패")
	void null_뉴스ID_검증_실패() {
		boolean result = validator.isValid(null, null);

		assertThat(result).isFalse();
	}
}