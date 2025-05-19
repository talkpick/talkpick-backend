package com.likelion.backendplus4.talkpick.backend.news.info.application.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
/**
 * NewsInfoDetail 도메인 객체를 NewsInfoDetailResponse DTO로 변환하는 매퍼 클래스입니다.
 *
 * @since 2025-05-14
 */
public class NewsInfoDetailResponseMapper {

	/**
	 * 도메인 객체를 응답 DTO로 변환합니다.
	 *
	 * @param newsInfoDetail 뉴스 상세 도메인 객체
	 * @return 뉴스 상세 응답 DTO
	 * @author 함예정
	 * @since 2025-05-14
	 */
	public static NewsInfoDetailResponse toResponseFromDomain(NewsInfoDetail newsInfoDetail) {
		return NewsInfoDetailResponse
			.builder()
			.newsId(newsInfoDetail.getNewsId())
			.category(newsInfoDetail.getCategory())
			.title(newsInfoDetail.getTitle())
			.content(newsInfoDetail.getContent())
			.originLink(newsInfoDetail.getOriginLink())
			.publishDate(newsInfoDetail.getPubDate())
			.imageUrl(newsInfoDetail.getImageUrl())
			.summary(newsInfoDetail.getSummary())
			.viewCount(newsInfoDetail.getViewCount())
			.build();
	}
}
