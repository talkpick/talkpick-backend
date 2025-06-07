package com.likelion.backendplus4.talkpick.backend.news.info.application.mapper;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.HighlightSegment;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;

/**
 * NewsInfoDetail 도메인 객체를 NewsInfoDetailResponse DTO로 변환하는 매퍼 클래스입니다.
 *
 * @since 2025-05-14
 */
public class NewsInfoCompleteMapper {

	/**
	 * 도메인 객체를 응답 DTO로 변환합니다.
	 *
	 * @param newsInfoDetail 뉴스 상세 도메인 객체
	 * @return NewsInfoDetailResponse
	 * @author 정안식
	 * @since 2025-05-28
	 */
	public static NewsInfoComplete toNewsInfoComplete(NewsInfoDetail newsInfoDetail, List<HighlightSegment> highlightSegments,Long currentViewCount) {

		return NewsInfoComplete
				.builder()
				.newsId(newsInfoDetail.getNewsId())
				.title(newsInfoDetail.getTitle())
				.originLink(newsInfoDetail.getOriginLink())
				.publishDate(newsInfoDetail.getPubDate())
				.category(newsInfoDetail.getCategory())
				.content(newsInfoDetail.getContent())
				.imageUrl(newsInfoDetail.getImageUrl())
				.summary(newsInfoDetail.getSummary())
				.viewCount(currentViewCount)
				.highlights(highlightSegments)
				.build();
	}

	public static NewsInfoComplete toNewsInfoCompleteByUserId(NewsInfoDetail newsInfoDetail, List<HighlightSegment> highlightSegments) {

		return NewsInfoComplete
			.builder()
			.newsId(newsInfoDetail.getNewsId())
			.title(newsInfoDetail.getTitle())
			.originLink(newsInfoDetail.getOriginLink())
			.publishDate(newsInfoDetail.getPubDate())
			.category(newsInfoDetail.getCategory())
			.content(newsInfoDetail.getContent())
			.imageUrl(newsInfoDetail.getImageUrl())
			.summary(newsInfoDetail.getSummary())
			.viewCount(newsInfoDetail.getViewCount())
			.highlights(highlightSegments)
			.build();
	}
}
