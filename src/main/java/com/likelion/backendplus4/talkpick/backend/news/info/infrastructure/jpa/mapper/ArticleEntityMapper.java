package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ArticleEntity;

/**
 * ArticleEntity를 NewsInfoDetail 도메인 객체로 변환하는 매퍼 클래스입니다.
 *
 * @since 2025-05-14
 */
public class ArticleEntityMapper {
	public static NewsInfoDetail toDetailFromEntity(ArticleEntity entity){
		return NewsInfoDetail.builder()
			.newsId(entity.getGuid())
			.category(entity.getCategory())
			.title(entity.getTitle())
			.content(entity.getDescription())
			.originLink(entity.getLink())
			.pubDate(entity.getPubDate())
			.build();
	}
}
