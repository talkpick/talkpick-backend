package com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.request;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 뉴스 검색 시 필요한 검색어와 페이지 정보를 담는 요청 DTO
 *
 * @since 2025-05-15
 */

public record SimilarNewsSearchRequest(
	String newsId) {
}