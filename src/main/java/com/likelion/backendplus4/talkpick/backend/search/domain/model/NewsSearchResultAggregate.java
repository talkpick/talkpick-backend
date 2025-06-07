package com.likelion.backendplus4.talkpick.backend.search.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

/**
 * 검색된 뉴스 결과를 표현하는 도메인 모델
 *
 * @since 2025-05-15
 */
@Getter
@Builder
public class NewsSearchResultAggregate {
	List<NewsSearchResult> newsSearchResultList;
	Long totalHits;
}
