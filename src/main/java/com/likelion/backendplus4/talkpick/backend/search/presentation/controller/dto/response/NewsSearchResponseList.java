package com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 뉴스 검색 결과 목록과 총 개수를 담는 응답 DTO
 *
 * @since 2025-05-15
 */
@Getter
@RequiredArgsConstructor
@Builder
public class NewsSearchResponseList {
	private final List<NewsSearchResponse> newsSearchResponseList;
	private final int total;
}