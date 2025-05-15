package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;

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