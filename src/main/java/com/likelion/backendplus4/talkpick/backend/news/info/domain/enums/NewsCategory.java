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
	ALL("전체"),
	POLITICS("정치"),
	SOCIETY("사회"),
	ECONOMY("경제"),
	SPORTS("스포츠"),
	INTERNATIONAL("국제"),
	ENTERTAINMENT("연예");

	private final String categoryName;

	NewsCategory(String categoryName) {
		this.categoryName = categoryName;
	}

	public String getcategoryName() {
		return categoryName;
	}
}