package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsInfoProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;

import lombok.RequiredArgsConstructor;


/**
 * 뉴스 정보 조회 기능을 제공하는 서비스 클래스.
 * NewsInfoProviderUseCase를 구현하여 외부 요청을 처리하며,
 * 실제 데이터 조회는 NewsInfoProviderPort를 통해 수행된다.
 *
 * @since 2025-05-16
 * @modified 2025-05-26
 */
@Service
@RequiredArgsConstructor
public class NewsInfoProviderService implements NewsInfoProviderUseCase {
	private final NewsInfoProviderPort newsInfoProviderPort;

	/**
	 * 최신 뉴스 정보를 마지막 ID 기준으로 페이지 단위로 조회한다.
	 *
	 * @param lastId 마지막으로 조회된 뉴스 ID (null일 경우 처음부터 조회)
	 * @param pageSize 한 페이지에 포함될 뉴스 개수
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-16
	 * @modified 2025-05-26
	 */
	@Override
	public SliceResult<NewsInfo> getLatestNewsInfo(String lastId, int pageSize) {
		return newsInfoProviderPort.getLatestNewsInfo(lastId, pageSize);
	}

	/**
	 * 지정한 카테고리의 최신 뉴스 정보를 마지막 ID 기준으로 페이지 단위로 조회한다.
	 *
	 * @param category 뉴스 카테고리
	 * @param lastId 마지막으로 조회된 뉴스 ID (null일 경우 처음부터 조회)
	 * @param pageSize 한 페이지에 포함될 뉴스 개수
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-16
	 * @modified 2025-05-26
	 */
	@Override
	public SliceResult<NewsInfo> getLatestNewsInfoByCategory(String category, String lastId, int pageSize) {
		return newsInfoProviderPort.getLatestNewsInfoByCategory(category, lastId, pageSize);
	}
}
