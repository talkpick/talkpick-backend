package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.news.info.application.command.ScrapCommand;
import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoCompleteMapper.toNewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * NewsInfoProviderUseCase를 구현하는 서비스 클래스입니다.
 * 뉴스 상세 정보 조회 및 조회수를 함께 제공합니다.
 *
 * @since 2025-05-14
 * @modified 2025-05-19
 */
@Service
@RequiredArgsConstructor
public class NewsInfoDetailProviderService implements NewsInfoDetailProviderUseCase {
	private final NewsDetailProviderPort newsDetailProviderPort;
	private final NewsViewCountIncreaseUseCase newsViewCountIncreaseUseCase;
	private final HighlightCalculator highlightCalculator;
	/**
	 * 뉴스 ID를 기반으로 뉴스 상세 정보와 현재 조회수를 함께 조회합니다.
	 *
	 * @param newsId 조회할 뉴스의 ID
	 * @return 뉴스 상세 정보와 조회수가 포함된 응답 객체
	 * @since 2025-05-19 최초 작성
	 * @author 양병학
	 */
	@Override
	public NewsInfoComplete getNewsInfoDetailByNewsId(String newsId) {
		NewsInfoDetail newsInfoDetail = fetchNewsInfoDetail(newsId);
		List<HighlightSegment> highlightSegments =  highlightCalculator.computeSegments(newsInfoDetail.getScrapInfos());
		Long currentViewCount = fetchCurrentViewCount(newsId);

		return combineNewsInfoAndViewCount(newsInfoDetail, highlightSegments, currentViewCount);
	}

	@Override
	public void saveScrap(ScrapCommand scrapCommand) {
		newsDetailProviderPort.saveScrap(scrapCommand);
	}

	/**
	 * 뉴스 상세 정보를 조회합니다.
	 *
	 * @param newsId 조회할 뉴스의 ID
	 * @return 뉴스 상세 도메인 객체
	 */
	private NewsInfoDetail fetchNewsInfoDetail(String newsId) {
		return newsDetailProviderPort.getNewsInfoDetailsByArticleId(newsId);
	}

	/**
	 * 뉴스의 현재 조회수를 조회합니다.
	 *
	 * @param newsId 조회할 뉴스의 ID
	 * @return 현재 조회수
	 */
	private Long fetchCurrentViewCount(String newsId) {
		return newsViewCountIncreaseUseCase.getCurrentViewCount(newsId);
	}

	/**
	 * 뉴스 상세 정보와 현재 조회수를 통합한 응답 객체를 생성합니다.
	 *
	 * @param newsInfoDetail 뉴스 상세 도메인 객체
	 * @param currentViewCount 현재 조회수
	 * @return 통합된 응답 객체
	 */

	private NewsInfoComplete combineNewsInfoAndViewCount(NewsInfoDetail newsInfoDetail, List<HighlightSegment> highlightSegments, Long currentViewCount) {
		return toNewsInfoComplete(newsInfoDetail, highlightSegments, currentViewCount);
	}
}