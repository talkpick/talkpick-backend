package com.likelion.backendplus4.talkpick.backend.news.info.presentation.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;

public class NewsInfoDetailResponseMapper {

	private NewsInfoDetailResponseMapper() {}

	public static NewsInfoDetailResponse toResponse(NewsInfoComplete complete) {
		return NewsInfoDetailResponse.builder()
			.newsId(complete.getNewsId())
			.title(complete.getTitle())
			.originLink(complete.getOriginLink())
			.publishDate(complete.getPublishDate())
			.category(complete.getCategory())
			.content(complete.getContent())
			.imageUrl(complete.getImageUrl())
			.summary(complete.getSummary())
			.highlightSegments(complete.getHighlights())
			.build();
	}
}
