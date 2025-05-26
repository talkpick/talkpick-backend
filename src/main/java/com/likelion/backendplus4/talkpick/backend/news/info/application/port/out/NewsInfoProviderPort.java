package com.likelion.backendplus4.talkpick.backend.news.info.application.port.out;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;

/**
 * 뉴스 정보를 외부 저장소(JPA, API 등)로부터 조회하기 위한 포트 인터페이스.
 * 최신 뉴스 정보 및 카테고리별 뉴스 정보를 슬라이스 방식으로 제공한다.
 *
 * @since 2025-05-16
 * @modified 2025-05-26
 */
public interface NewsInfoProviderPort {

	/**
	 * 최신 뉴스 정보를 마지막 뉴스 ID를 기준으로 제한된 개수만큼 조회한다.
	 *
	 * @param lastNewsId 마지막으로 조회된 뉴스 ID (null일 경우 처음부터 조회)
	 * @param limit 조회할 뉴스 개수 제한
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-16
	 */
	SliceResult<NewsInfo> getLatestNewsInfo(String lastNewsId, int limit);

	/**
	 * 지정한 카테고리에 해당하는 최신 뉴스 정보를 마지막 뉴스 ID를 기준으로 제한된 개수만큼 조회한다.
	 *
	 * @param category 뉴스 카테고리
	 * @param lastNewsId 마지막으로 조회된 뉴스 ID (null일 경우 처음부터 조회)
	 * @param limit 조회할 뉴스 개수 제한
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-26
	 */
	SliceResult<NewsInfo> getLatestNewsInfoByCategory(String category, String lastNewsId, int limit);
}
