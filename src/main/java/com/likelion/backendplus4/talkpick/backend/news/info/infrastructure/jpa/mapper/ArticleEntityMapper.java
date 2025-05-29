package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ArticleEntity;

/**
 * ArticleEntity를 NewsInfoDetail 도메인 객체로 변환하는 매퍼 클래스입니다.
 *
 * @since 2025-05-14
 */
public class ArticleEntityMapper {
    public static NewsInfoDetail toDetailFromEntity(ArticleEntity entity) {
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
