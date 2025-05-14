package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;

/**
 * 뉴스 상세 정보를 표현하는 도메인 모델 클래스입니다.
 *
 * @since 2025-05-14
 */
@Builder
public record NewsInfoDetail(
	String newsId,
	String category,
	String title,
	String content,
	String originLink,
	LocalDateTime pubDate
) {
}