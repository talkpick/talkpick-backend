package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.application.port.out;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;

/**
 * 뉴스의 상세 정보를 인프라 계층에서 조회하기 위한 포트 인터페이스입니다.
 *
 * @since 2025-05-14
 */
public interface NewsDetailProviderPort {

	/**
	 * 주어진 guid(뉴스 ID)를 기반으로 뉴스 상세 정보를 조회합니다.
	 *
	 * @param guid 뉴스 고유 식별자
	 * @return 뉴스 상세 도메인 객체
	 * @author 함예정
	 * @since 2025-05-14
	 */
	NewsInfoDetail getNewsInfoDetailsByArticleId(String guid);
}
