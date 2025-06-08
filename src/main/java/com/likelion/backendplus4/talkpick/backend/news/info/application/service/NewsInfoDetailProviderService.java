package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogMethodValues;
import com.likelion.backendplus4.talkpick.backend.news.info.application.command.ScrapCommand;
import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoCompleteMapper.*;
import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoDynamicMapper.toNewsInfoDynamic;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.support.HighlightCalculator;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.enums.NewsCategory;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.HighlightSegment;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDynamic;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;

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
	@EntryExitLog
	@LogMethodValues
	@Override
	public NewsInfoComplete getNewsInfoDetailByNewsId(String newsId) {
		NewsInfoDetail detail = fetchNewsInfoDetail(newsId);

		List<HighlightSegment> highlightSegments =  highlightCalculator.computeSegments(detail.getScrapInfos());

		return toNewsInfoComplete(detail, highlightSegments);
	}

	/**
	 * 별도의 서비스 분리 하지않음
	 * detail를 보겠다는 기능적관점으로볼떈 같은서비스
	 * TODO: 이 메서드 주석추가
	 */
	public NewsInfoDynamic getNewsInfoDynamic(String newsId, String category, LocalDateTime publishDate) {
		Long viewCount = newsViewCountIncreaseUseCase.increaseViewCount(newsId, category, publishDate);
		return toNewsInfoDynamic(newsId, viewCount);
	}

	@EntryExitLog
	@Override
	public List<NewsInfoComplete> getNewsInfoDetailByUserId(Long userId) {

		List<NewsInfoDetail> details = fetchNewsInfoDetailWithUserId(userId);

		return details.stream()
			.map(detail ->
				combineNewsInfoByUserId(
					detail,
					highlightCalculator.computeSegments(detail.getScrapInfos())
				)
			)
			.toList();
	}

	@Override
	public NewsInfoDetail getNewsDetail(String newsId) {
		return fetchNewsInfoDetail(newsId);
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
		return newsDetailProviderPort
			.getNewsInfoDetailsByArticleId(newsId)
			.orElseThrow(() -> new NewsInfoException(NewsInfoErrorCode.NEWS_NOT_FOUND));
	}

	private List<NewsInfoDetail> fetchNewsInfoDetailWithUserId(Long userId) {
		return newsDetailProviderPort
			.getNewsInfoDetailsByUserId(userId);
	}

	private NewsInfoComplete combineNewsInfoByUserId(NewsInfoDetail newsInfoDetail, List<HighlightSegment> highlightSegments) {
		return toNewsInfoCompleteByUserId(newsInfoDetail, highlightSegments);
	}
}