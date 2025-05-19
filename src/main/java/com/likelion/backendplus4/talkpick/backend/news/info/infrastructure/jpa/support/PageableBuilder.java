package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.support;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/**
 * Pageable 객체를 생성하는 유틸리티 클래스입니다.
 * 페이지 번호, 페이지 크기, 정렬 조건을 기반으로 PageRequest 객체를 반환합니다.
 *
 * @since 2025-05-19
 */
public class PageableBuilder {

	/**
	 * 페이지 번호, 페이지 크기, 정렬 조건을 기준으로 Pageable 객체를 생성합니다.
	 *
	 * @param currentPage 조회할 페이지 번호 (0부터 시작)
	 * @param pageSize 페이지당 항목 수
	 * @param sort 정렬 조건
	 * @return 지정된 조건에 맞는 PageRequest 객체
	 * @author 함예정
	 * @since 2025-05-19
	 */
	public static Pageable createPageable(int currentPage, int pageSize, Sort sort) {
		return PageRequest.of(currentPage, pageSize, sort);
	}
}
