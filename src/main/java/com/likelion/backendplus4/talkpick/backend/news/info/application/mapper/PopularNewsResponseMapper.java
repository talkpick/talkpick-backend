package com.likelion.backendplus4.talkpick.backend.news.info.application.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;

public class PopularNewsResponseMapper {

	public static PopularNewsResponse toResponse(NewsInfoDetail newsDetail) {
		return new PopularNewsResponse(
			newsDetail.getNewsId(),
			newsDetail.getTitle(),
			newsDetail.getContent(),
			newsDetail.getImageUrl(),
			newsDetail.getCategory()
		);
	}
}