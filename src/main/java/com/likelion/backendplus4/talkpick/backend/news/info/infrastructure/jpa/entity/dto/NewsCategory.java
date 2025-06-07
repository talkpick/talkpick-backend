package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.dto;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;

/**
 * 뉴스 카테고리 열거형.
 * 각 카테고리는 사용자에게 보여줄 표시 이름(displayName)을 가진다.
 *
 * @since 2025-05-26
 */
public enum NewsCategory {
	POLITICS("정치"),
	ECONOMY("경제"),
	SOCIETY("사회"),
	INTERNATIONAL("국제"),
	ENTERTAINMENT("연예"),
	SPORTS("스포츠");

	private final String displayName;

	NewsCategory(String displayName) {
		this.displayName = displayName;
	}

	public String getDisplayName() {
		return displayName;
	}

	/**
	 * 카테고리 코드(String)를 받아 해당 열거형의 표시 이름을 반환한다.
	 * 코드가 잘못된 경우 NewsInfoException 발생.
	 *
	 * @param code 카테고리 코드 (예: "politics", "ECONOMY")
	 * @return 대응되는 카테고리의 표시 이름
	 * @throws NewsInfoException 지원하지 않는 카테고리 코드일 경우
	 * @author 함예정
	 * @since 2025-05-26
	 */
	public static String displayNameOf(String code) {
		try {
			return NewsCategory
				.valueOf(code.trim().toUpperCase())
				.getDisplayName();
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new NewsInfoException(NewsInfoErrorCode.NEWS_CATEGORY_NOT_FOUND);
		}
	}
}
