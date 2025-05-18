package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;

/**
 * 뉴스 정보를 표현하는 도메인 모델
 *
 * @since 2025-05-15
 * @modified 2025-05-18
 */
public record NewsInfo(
	@NotNull
	String id,

	@NotNull
	String category,

	@NotNull
	String title,

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	LocalDateTime publishedAt,

	String imageUrl,

	@NotNull
	String summary) {
}
