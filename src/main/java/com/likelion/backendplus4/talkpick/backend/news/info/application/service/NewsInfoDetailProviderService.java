package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoDetailResponseMapper.*;

import com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoDetailResponseMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.application.port.out.NewsDetailProviderPort;

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
	private final NewsViewCountIncreaseUseCase newsViewCountIncreaseUseCase; // 추가

	@Override
	public NewsInfoDetailResponse getNewsInfoDetailByNewsId(String newsId) {

		NewsInfoDetail newsInfoDetail = newsDetailProviderPort.getNewsInfoDetailsByArticleId(newsId);
		Long currentViewCount = newsViewCountIncreaseUseCase.getCurrentViewCount(newsId);
		NewsInfoDetailResponse response = NewsInfoDetailResponseMapper.toResponseFromDomain(newsInfoDetail);

		return new NewsInfoDetailResponse(
				response.newsId(),
				response.category(),
				response.title(),
				response.content(),
				response.originLink(),
				response.publishDate(),
				currentViewCount
		);
	}
}
