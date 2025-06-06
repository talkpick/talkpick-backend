package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import lombok.Builder;
import lombok.Getter;

/**
 * 뉴스 상세 정보를 표현하는 도메인 모델 클래스입니다.
 *
 * @since 2025-05-14
 */
@Builder
@Getter
public class NewsInfoDetail {
	private String newsId;
	private String title;
	private String originLink;
	private LocalDateTime pubDate;
	private String category;
	private String content;
	private String imageUrl;
	private String summary;
	private Long viewCount;
	private List<ScrapInfo> scrapInfos;
}