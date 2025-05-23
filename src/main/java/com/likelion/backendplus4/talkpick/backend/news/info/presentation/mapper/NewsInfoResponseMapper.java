package com.likelion.backendplus4.talkpick.backend.news.info.presentation.mapper;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.dto.NewsInfoResponse;

public class NewsInfoResponseMapper {
	public static SliceResponse<NewsInfoResponse> toSliceResponse(SliceResult<NewsInfo> domains) {
		List<NewsInfoResponse> content = domains.content()
			.stream()
			.map(NewsInfoResponseMapper::toNewsInfoResponse)
			.toList();
		boolean hasNext = domains.hasNext();

		return new SliceResponse<>(content, hasNext);
	}

	public static NewsInfoResponse toNewsInfoResponse(NewsInfo domain) {
		return NewsInfoResponse.builder()
			.id(domain.getId())
			.category(domain.getCategory())
			.title(domain.getTitle())
			.publishedAt(domain.getPublishedAt())
			.imageUrl(domain.getImageUrl())
			.content(domain.getContent())
			.build();
	}
}
