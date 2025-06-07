package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.response;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record NewsInfoResponse(
	String id,
	String category,
	String title,
	LocalDateTime publishedAt,
	String imageUrl,
	String content) {
}
