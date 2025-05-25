package com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NewsIdConstraint 어노테이션의 통합 테스트 클래스입니다.
 *
 * @since 2025-05-25
 * @author 양병학
 */
@DisplayName("뉴스 ID Constraint 어노테이션 테스트")
class NewsIdConstraintTest {

	private Validator validator;

	@BeforeEach
	void setUp() {
		ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		validator = factory.getValidator();
	}

	@DisplayName("유효한 뉴스 ID로 검증 성공")
	@ParameterizedTest
	@ValueSource(strings = {"KM123", "DA456", "KH789"})
	void 유효한_뉴스ID로_검증_성공(String validNewsId) {
		TestObject testObject = new TestObject(validNewsId);

		Set<ConstraintViolation<TestObject>> violations = validator.validate(testObject);

		assertThat(violations).isEmpty();
	}

	@DisplayName("잘못된 뉴스 ID로 검증 실패")
	@ParameterizedTest
	@ValueSource(strings = {"km123", "AB123", "KM", "123KM"})
	void 잘못된_뉴스ID로_검증_실패(String invalidNewsId) {
		TestObject testObject = new TestObject(invalidNewsId);

		Set<ConstraintViolation<TestObject>> violations = validator.validate(testObject);

		assertThat(violations).hasSize(1);
		assertThat(violations.iterator().next().getMessage())
			.contains("뉴스 ID는 유효한 형식이어야 합니다");
	}

	private static class TestObject {
		@NewsIdConstraint
		private final String newsId;

		public TestObject(String newsId) {
			this.newsId = newsId;
		}
	}
}