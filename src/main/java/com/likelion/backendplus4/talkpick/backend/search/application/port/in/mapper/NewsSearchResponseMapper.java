package com.likelion.backendplus4.talkpick.backend.search.application.port.in.mapper;

import java.util.List;
import java.util.stream.Collectors;

import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResult;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResultAggregate;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.response.NewsSearchResponse;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.response.NewsSearchResponseList;

/**
 * 도메인 모델을 응답 DTO로 변환하는 매퍼 클래스
 *
 * @author 정안식
 * @since 2025-05-15
 */
public class NewsSearchResponseMapper {

	/**
	 * 단일 도메인 모델을 응답 DTO로 변환한다.
	 *
	 * @param d 변환할 도메인 모델
	 * @return 변환된 응답 DTO
	 * @author 정안식
	 * @since 2025-05-15
	 */
	public static NewsSearchResponse toResponse(NewsSearchResult d) {
		return NewsSearchResponse.builder()
			.newsId(d.getNewsId())
			.title(d.getTitle())
			.content(d.getContent())
			.publishedAt(d.getPublishedAt())
			.imageUrl(d.getImageUrl())
			.category(d.getCategory())
			.build();
	}

	/**
	 * 도메인 모델 리스트를 응답 DTO 리스트로 변환한다.
	 *
	 * @param domainList 변환할 도메인 모델 리스트
	 * @return 응답 DTO 리스트가 포함된 NewsSearchResponseList
	 * @author 정안식
	 * @since 2025-05-15
	 */
	public static NewsSearchResponseList toListResponse(List<NewsSearchResult> domainList) {
		List<NewsSearchResponse> items = domainList.stream()
			.map(NewsSearchResponseMapper::toResponse)
			.collect(Collectors.toList());
		return NewsSearchResponseList.builder()
			.newsSearchResponseList(items)
			.total((long) domainList.size())
			.build();
	}

	public static NewsSearchResponseList toListResponse(NewsSearchResultAggregate newsSearchResultAggregate) {
		List<NewsSearchResponse> items = newsSearchResultAggregate.getNewsSearchResultList().stream()
			.map(NewsSearchResponseMapper::toResponse)
			.collect(Collectors.toList());
		return NewsSearchResponseList.builder()
			.newsSearchResponseList(items)
			.total(newsSearchResultAggregate.getTotalHits())
			.build();
	}
}

