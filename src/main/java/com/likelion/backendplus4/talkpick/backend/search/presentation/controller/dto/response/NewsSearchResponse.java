package com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 단일 뉴스 검색 결과를 나타내는 응답 DTO
 *
 * @since 2025-05-15
 */
@Getter
@RequiredArgsConstructor
@Builder
public class NewsSearchResponse {
	private final String newsId;
	private final String title;
	private final String content;
	private final LocalDateTime publishedAt;
	private final String imageUrl;
	private final String category;
}