package com.likelion.backendplus4.talkpick.backend.news.info.application.port.in;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;

/**
 * 뉴스 정보를 조회하기 위한 유스케이스 인터페이스
 * 최신 뉴스 정보 및 카테고리별 최신 뉴스 정보를 슬라이스 방식으로 제공한다.
 *
 * @since 2025-05-16
 * @modified 2025-05-26
 */
public interface NewsInfoProviderUseCase {

	/**
	 * 최신 뉴스 정보를 마지막 ID 기준으로 페이지 단위로 조회한다.
	 *
	 * @param lastId 마지막으로 조회된 뉴스 ID (null일 경우 처음부터 조회)
	 * @param pageSize 한 페이지에 포함될 뉴스 개수
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-16
	 */
	SliceResult<NewsInfo> getLatestNewsInfo(String lastId, int pageSize);

	/**
	 * 카테고리별 최신 뉴스 정보를 마지막 ID 기준으로 페이지 단위로 조회한다.
	 *
	 * @param category 뉴스 카테고리
	 * @param lastId 마지막으로 조회된 뉴스 ID (null일 경우 처음부터 조회)
	 * @param pageSize 한 페이지에 포함될 뉴스 개수
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-26
	 */
	SliceResult<NewsInfo> getLatestNewsInfoByCategory(String category, String lastId, int pageSize);
}
