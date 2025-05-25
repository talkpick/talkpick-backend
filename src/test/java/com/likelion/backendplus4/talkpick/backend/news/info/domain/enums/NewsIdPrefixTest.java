package com.likelion.backendplus4.talkpick.backend.news.info.domain.enums;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * NewsIdPrefix enum의 단위 테스트 클래스
 *
 * @since 2025-05-25
 * @author 양병학
 */
@DisplayName("뉴스 ID 접두사 Enum 테스트")
class NewsIdPrefixTest {

	@DisplayName("유효한 접두사 검증 성공")
	@ParameterizedTest
	@ValueSource(strings = {"KM", "DA", "KH"})
	void 유효한_접두사_검증_성공(String validPrefix) {
		boolean result = NewsIdPrefix.isValidPrefix(validPrefix);

		assertThat(result).isTrue();
	}

	@DisplayName("유효하지 않은 접두사 검증 실패")
	@ParameterizedTest
	@ValueSource(strings = {"AB", "XX", "km", "da", "kh", "", "KMA"})
	void 유효하지_않은_접두사_검증_실패(String invalidPrefix) {
		boolean result = NewsIdPrefix.isValidPrefix(invalidPrefix);

		assertThat(result).isFalse();
	}

	@Test
	@DisplayName("모든 접두사 배열 반환 확인")
	void 모든_접두사_배열_반환_확인() {
		String[] allPrefixes = NewsIdPrefix.getAllPrefixes();

		assertThat(allPrefixes).hasSize(3);
		assertThat(allPrefixes).containsExactlyInAnyOrder("KM", "DA", "KH");
	}

	@Test
	@DisplayName("각 Enum 값의 속성 확인")
	void 각_Enum_값의_속성_확인() {
		assertThat(NewsIdPrefix.KM.getPrefix()).isEqualTo("KM");
		assertThat(NewsIdPrefix.KM.getDescription()).isEqualTo("국민일보");

		assertThat(NewsIdPrefix.DA.getPrefix()).isEqualTo("DA");
		assertThat(NewsIdPrefix.DA.getDescription()).isEqualTo("동아일보");

		assertThat(NewsIdPrefix.KH.getPrefix()).isEqualTo("KH");
		assertThat(NewsIdPrefix.KH.getDescription()).isEqualTo("경향신문");
	}
}