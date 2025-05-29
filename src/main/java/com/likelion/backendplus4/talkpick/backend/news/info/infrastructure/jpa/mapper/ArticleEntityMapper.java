package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.mapper;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.ScrapInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ArticleEntity;

/**
 * ArticleEntity를 NewsInfoDetail 도메인 객체로 변환하는 매퍼 클래스입니다.
 *
 * @since 2025-05-14
 * @modified 2025-05-27
 * 25-05-27 - 도메인 객체가 ScrapInfo를 포함하도록 수정
 */
public class ArticleEntityMapper {
	public static NewsInfoDetail toInfoDetailFromData(ArticleEntity entity) {
		List<ScrapInfo> scrapInfos = entity.getScrapEntities().stream()
			.map(ScrapEntityMapper::toDomain)
			.toList();

		return NewsInfoDetail.builder()
			.newsId(entity.getGuid())
			.title(entity.getTitle())
			.originLink(entity.getLink())
			.pubDate(entity.getPubDate())
			.category(entity.getCategory())
			.content(entity.getDescription())
			.imageUrl(entity.getImageUrl())
			.summary(entity.getSummary())
			.viewCount(entity.getViewCount())
			.scrapInfos(scrapInfos)
			.build();
	}
	
	public static NewsInfo toInfoFromEntity(ArticleEntity e) {
		return NewsInfo.builder()
			.id(e.getGuid())
			.category(e.getCategory())
			.title(e.getTitle())
			.publishedAt(e.getPubDate())
			.imageUrl(e.getImageUrl())
			.content(e.getDescription())
			.build();
	}
}
