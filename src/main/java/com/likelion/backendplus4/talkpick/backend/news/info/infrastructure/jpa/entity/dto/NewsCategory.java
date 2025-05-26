package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.dto;

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


	public static String displayNameOf(String code) {
		try {
			return NewsCategory
				.valueOf(code.trim().toUpperCase())
				.getDisplayName();
		} catch (IllegalArgumentException | NullPointerException e) {
			throw new IllegalArgumentException("지원하지 않는 카테고리 코드: " + code);
		}
	}
}
