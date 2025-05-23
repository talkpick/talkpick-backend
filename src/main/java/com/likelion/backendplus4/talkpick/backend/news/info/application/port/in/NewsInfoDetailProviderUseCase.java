package com.likelion.backendplus4.talkpick.backend.news.info.application.port.in;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;

/**
 *  뉴스 상세 정보를 조회하는 UseCase입니다
 *
 * @since 2025-05-14
 */
public interface NewsInfoDetailProviderUseCase {
	/**
	 * 주어진 뉴스 ID를 기반으로 뉴스 상세 정보를 조회합니다.
	 *
	 * @param newsId 뉴스 고유 ID
	 * @return 뉴스 상세 도메인 객체
	 *
	 * @author 함예정
	 * @since 2025-05-14
	 */
	NewsInfoDetailResponse getNewsInfoDetailByNewsId(String newsId);
}
