package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoDetailResponseMapper.*;

import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;

import lombok.RequiredArgsConstructor;

/**
 * NewsInfoProviderUseCase를 구현하는 서비스 클래스입니다.
 * newsDetailProviderPort를 통해 뉴스 상세 정보를 조회합니다.
 *
 * @since 2025-05-14
 */
@Service
@RequiredArgsConstructor
public class NewsInfoDetailProviderService implements NewsInfoDetailProviderUseCase {
	private final NewsDetailProviderPort newsDetailProviderPort;

	/**
	 * 주어진 뉴스 ID를 기반으로 뉴스 상세 정보를 조회합니다.
	 *
	 * @param newsId 뉴스 고유 ID
	 * @return 뉴스 상세 도메인 객체
	 *
	 * @author 함예정
	 * @since 2025-05-14
	 */
	@Override
	public NewsInfoDetailResponse getNewsInfoDetailByNewsId(String newsId) {
		NewsInfoDetail newsInfoDetail = newsDetailProviderPort.getNewsInfoDetailsByArticleId(newsId);
		return toResponseFromDomain(newsInfoDetail);
	}
}
