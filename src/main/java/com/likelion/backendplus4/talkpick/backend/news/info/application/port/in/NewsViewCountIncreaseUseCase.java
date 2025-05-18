package com.likelion.backendplus4.talkpick.backend.news.info.application.port.in;

public interface NewsViewCountIncreaseUseCase {
	/**
	 * 뉴스 조회수를 증가시킵니다.
	 *
	 * @param newsId 뉴스 ID
	 * @param ipAddress 사용자 IP 주소 (중복 조회 방지용)
	 */
	void increaseViewCount(String newsId, String ipAddress);
}