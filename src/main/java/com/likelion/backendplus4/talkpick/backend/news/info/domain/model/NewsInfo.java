package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

/**
 * 뉴스 정보를 표현하는 도메인 모델
 *
 * @since 2025-05-15
 * @modified 2025-05-18
 */
@Builder
@Getter
public class NewsInfo {
	@NotNull
	private String id;

	@NotNull
	private String category;

	@NotNull
	private String title;

	@NotNull
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime publishedAt;

	private String imageUrl;

	@NotNull
	private String content;
}
