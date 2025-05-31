package com.likelion.backendplus4.talkpick.backend.news.info.application.port.in;

import java.time.LocalDateTime;

public interface NewsViewCountIncreaseUseCase {
	/**
	 * 뉴스 조회수를 증가시킵니다.
	 *
	 * @param newsId 뉴스 ID
	 */
	Long increaseViewCount(String newsId, String category, LocalDateTime publishDate);


	/**
	 * 뉴스의 현재 조회수를 조회합니다.
	 *
	 * @param newsId 뉴스 ID
	 * @return 현재 조회수
	 */
	Long getCurrentViewCount(String newsId);
}