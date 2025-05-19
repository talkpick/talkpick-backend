package com.likelion.backendplus4.talkpick.backend.search.infrastructure.adapter.mapper;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResult;
import com.likelion.backendplus4.talkpick.backend.search.infrastructure.adapter.document.NewsSearchDocument;

/**
 * Elasticsearch 문서 모델을 뉴스 검색 도메인 모델로 변환하는 매퍼
 *
 * @since 2025-05-15
 */
@Component
public class NewsSearchDocumentMapper {
	/**
	 * 문서 모델의 Instant 타입 publishedAt을 LocalDateTime으로 변환하여 도메인 모델 생성
	 *
	 * @param doc Elasticsearch 문서 모델 객체
	 * @return 변환된 뉴스 검색 도메인 결과
	 * @author 정안식
	 * @since 2025-05-15
	 */
	public NewsSearchResult toDomain(NewsSearchDocument doc) {
		// Instant → LocalDateTime 변환
		LocalDateTime ldt = LocalDateTime.ofInstant(
			doc.getPublishedAt(),
			ZoneId.systemDefault()
		);
		return NewsSearchResult.builder()
			.newsId(doc.getNewsId())
			.title(doc.getTitle())
			.content(doc.getContent())
			.publishedAt(ldt)
			.imageUrl(doc.getImageUrl())
			.category(doc.getCategory())
			.build();
	}
}
