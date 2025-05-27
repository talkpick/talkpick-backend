// application/port/out/PopularNewsPort.java
package com.likelion.backendplus4.talkpick.backend.news.info.application.port.out;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;

/**
 * 인기뉴스 랭킹 조회를 위한 포트 인터페이스
 */
public interface PopularNewsPort {

	/**
	 * 특정 카테고리의 Top1 뉴스 ID 조회
	 */
	String getTop1NewsId(String category);

	/**
	 * 특정 카테고리의 Top1 뉴스와 점수 조회 (해시 계산용)
	 */
	String getTop1NewsWithScore(String category);

	/**
	 * 해시값 저장
	 */
	void saveRankingHash(String category, String hashValue);

	/**
	 * 저장된 해시값 조회
	 */
	String getSavedRankingHash(String category);

	/**
	 * 카테고리별 Top1 뉴스 결과 저장 (2차 캐싱)
	 */
	void saveTopNews(String category, PopularNewsResponse topNews);

	/**
	 * 카테고리별 Top1 뉴스 결과 조회 (2차 캐싱)
	 */
	PopularNewsResponse getTopNews(String category);
}