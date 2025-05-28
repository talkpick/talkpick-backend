package com.likelion.backendplus4.talkpick.backend.news.info.application.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.likelion.backendplus4.talkpick.backend.news.info.application.service.HighlightSegment;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.dto.ScrapInfoResponse;

import lombok.Builder;


/**
 * 뉴스 상세 정보를 클라이언트에 응답하기 위한 DTO 클래스입니다.
 *
 * @since 2025-05-14
 */
@Builder
public record NewsInfoDetailResponse(
	String newsId,
	String title,
	String originLink,
	LocalDateTime publishDate,
	String category,
	String content,
	String imageUrl,
	String summary,
	Long viewCount,
	List<HighlightSegment> highlightSegments
) {
}