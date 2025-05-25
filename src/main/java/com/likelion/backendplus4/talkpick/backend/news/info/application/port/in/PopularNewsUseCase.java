// application/port/in/PopularNewsUseCase.java
package com.likelion.backendplus4.talkpick.backend.news.info.application.port.in;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;

/**
 * 인기뉴스 조회 유스케이스
 */
public interface PopularNewsUseCase {

	/**
	 * 전체 및 카테고리별 Top1 뉴스 조회
	 */
	List<PopularNewsResponse> getTopNewsByAllCategories();
}