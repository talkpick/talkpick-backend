package com.likelion.backendplus4.talkpick.backend.news.info.application.port.in;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;

/**
 * 인기뉴스 조회 유스케이스입니다.
 *
 * @since 2025-05-27
 */
public interface PopularNewsUseCase {

	/**
	 * "전체" 카테고리의 Top1 인기뉴스를 조회합니다.
	 *
	 * 메인 페이지에서 호출되며, 전체 뉴스 중 가장 인기 있는 뉴스 1개를 반환합니다.
	 *
	 * @return "전체" 카테고리의 Top1 인기뉴스
	 * @since 2025-05-27
	 * @author 양병학
	 */
	PopularNewsResponse getTopNewsFromAllCategories();

	/**
	 * 특정 카테고리의 Top1 뉴스를 조회합니다.
	 *
	 * 단일 카테고리에 대해서만 캐싱 및 조회 로직을 수행합니다.
	 *
	 * @param categoryName 조회할 카테고리 한글명 (Redis/DB 조회용)
	 * @return 해당 카테고리의 Top1 인기뉴스
	 * @since 2025-05-27
	 * @author 양병학
	 */
	PopularNewsResponse getTopNewsByCategory(String categoryName);
}