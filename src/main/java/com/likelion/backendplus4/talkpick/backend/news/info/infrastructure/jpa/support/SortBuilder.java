package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.support;

import org.springframework.data.domain.Sort;

/**
 * 공통 정렬 조건을 생성하는 유틸리티 클래스입니다.
 * 다양한 정렬 기준 메소드를 정적(static) 메소드로 제공합니다.
 *
 * @since 2025-05-19
 */
public class SortBuilder {

	/**
	 * ID 기준 내림차순 정렬 조건을 생성합니다.
	 *
	 * @return ID 필드를 기준으로 내림차순 정렬하는 Sort 객체
	 * @author 함예정
	 * @since 2025-05-19
	 */
	public static Sort createSortByIdDesc() {
		return Sort.by(Sort.Direction.DESC, "id");
	}
}
