package com.likelion.backendplus4.talkpick.backend.news.info.application.dto;

import java.time.LocalDateTime;

import lombok.Builder;


/**
 * 뉴스 상세 정보를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 *
 * @since 2025-05-14
 */
@Builder
public record NewsInfoDetailResponse(
	String newsId,
	String category,
	String title,
	String content,
	String originLink,
	LocalDateTime publishDate
) {
}