package com.likelion.backendplus4.talkpick.backend.news.info.domain.enums;

import java.util.Arrays;

/**
 * 뉴스 ID 접두사를 관리하는 enum 클래스입니다.
 * 유효한 언론사 코드와 검증 로직을 제공합니다.
 *
 * @since 2025-05-25
 * @author 양병학
 */
public enum NewsIdPrefix {
	KM("KM", "국민일보"),
	DA("DA", "동아일보"),
	KH("KH", "경향신문");

	private final String prefix;
	private final String description;

	NewsIdPrefix(String prefix, String description) {
		this.prefix = prefix;
		this.description = description;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getDescription() {
		return description;
	}

	/**
	 * 주어진 접두사가 유효한지 검증합니다.
	 *
	 * @param prefix 검증할 접두사
	 * @return 유효하면 true, 아니면 false
	 */
	public static boolean isValidPrefix(String prefix) {
		return Arrays.stream(values()).anyMatch(p -> p.prefix.equals(prefix));
	}

	/**
	 * 모든 유효한 접두사 배열을 반환합니다.
	 *
	 * @return 접두사 문자열 배열
	 */
	public static String[] getAllPrefixes() {
		return Arrays.stream(values()).map(NewsIdPrefix::getPrefix).toArray(String[]::new);
	}
}