package com.likelion.backendplus4.talkpick.backend.news.info.application.port.in;

import java.time.LocalDateTime;

public interface NewsViewCountIncreaseUseCase {
	/**
	 * 뉴스 조회수를 증가시킵니다.
	 *
	 * @param newsId 뉴스 ID
	 */
	Long increaseViewCount(String newsId, Long currentViewCount, String category, LocalDateTime publishDate);
}