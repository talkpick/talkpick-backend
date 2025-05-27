package com.likelion.backendplus4.talkpick.backend.news.info.domain.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 뉴스 카테고리를 관리하는 enum 클래스입니다.
 * URL에는 영어명을, DB/Redis에는 한글명을 사용합니다.
 *
 * @since 2025-05-27
 */
public enum NewsCategory {
	ALL("all", "전체"),
	POLITICS("politics", "정치"),
	SOCIETY("society", "사회"),
	ECONOMY("economy", "경제"),
	SPORTS("sports", "스포츠"),
	INTERNATIONAL("international", "국제"),
	ENTERTAINMENT("entertainment", "연예");

	private final String englishName;
	private final String koreanName;

	NewsCategory(String englishName, String koreanName) {
		this.englishName = englishName;
		this.koreanName = koreanName;
	}

	/**
	 * 영어명을 반환합니다.
	 * URL 파라미터에서 사용됩니다.
	 *
	 * @return 영어 카테고리명
	 * @since 2025-05-27
	 * @author 양병학
	 */
	public String getEnglishName() {
		return englishName;
	}

	/**
	 * 한글명을 반환합니다.
	 * DB/Redis 조회 시 사용됩니다.
	 *
	 * @return 한글 카테고리명
	 * @since 2025-05-27
	 * @author 양병학
	 */
	public String getKoreanName() {
		return koreanName;
	}

	/**
	 * 모든 카테고리의 영어명 리스트를 반환합니다.
	 * API 문서화 등에서 사용됩니다.
	 *
	 * @return 영어 카테고리명 리스트
	 * @since 2025-05-27
	 * @author 양병학
	 */
	public static List<String> getAllEnglishNames() {
		return Arrays.stream(values())
			.map(NewsCategory::getEnglishName)
			.collect(Collectors.toList());
	}
}