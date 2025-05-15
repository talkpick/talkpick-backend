package com.likelion.backendplus4.talkpick.backend.search.infrastructure.adapter;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogMethodValues;
import com.likelion.backendplus4.talkpick.backend.search.application.port.out.NewsSearchRepositoryPort;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearch;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResult;
import com.likelion.backendplus4.talkpick.backend.search.infrastructure.adapter.document.NewsSearchDocument;
import com.likelion.backendplus4.talkpick.backend.search.infrastructure.adapter.mapper.NewsSearchDocumentMapper;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;

/**
 * Elasticsearch를 사용해 뉴스 검색 도메인의 searchByMatch를 구현하는 어댑터 클래스
 *
 * @since 2025-05-15
 */
@Component
public class ElasticsearchNewsSearchAdapter implements NewsSearchRepositoryPort {

	private final ElasticsearchOperations ops;
	private final NewsSearchDocumentMapper mapper;
	private final String indexName;

	public ElasticsearchNewsSearchAdapter(
		ElasticsearchOperations ops,
		NewsSearchDocumentMapper mapper,
		@Value("${news.index.name}") String indexName) {
		this.ops = ops;
		this.mapper = mapper;
		this.indexName = indexName;
	}

	/**
	 * 검색어와 페이징 정보가 담긴 NewsSearch 도메인 모델을 기반으로
	 * Elasticsearch에서 문서를 조회하여 도메인 결과 리스트로 반환한다.
	 *
	 * @param newsSearch 검색 조건과 페이징 정보가 담긴 도메인 모델
	 * @return 검색된 뉴스 도메인 결과 리스트
	 * @author 정안식
	 * @since 2025-05-15
	 */
	@EntryExitLog
	@LogMethodValues
	@Override
	public List<NewsSearchResult> searchByMatch(NewsSearch newsSearch) {
		NativeQuery query = buildNativeQuery(newsSearch);
		SearchHits<NewsSearchDocument> hits = executeSearch(query);
		return mapToDomain(hits);
	}

	/**
	 * 검색 조건에 맞는 Bool 쿼리를 생성하여 NativeQuery로 빌드한다.
	 *
	 * @param newsSearch 검색 조건과 페이징 정보 도메인 모델
	 * @return Elasticsearch NativeQuery 객체
	 * @author 정안식
	 * @since 2025-05-15
	 */
	private NativeQuery buildNativeQuery(NewsSearch newsSearch) {
		Query titleClause = match(m -> m
			.field("title")
			.analyzer("nori")
			.query(newsSearch.getQuery())
		);
		Query contentClause = match(m -> m
			.field("content")
			.analyzer("nori")
			.query(newsSearch.getQuery())
		);
		Query boolQuery = bool(b -> b
			.should(titleClause)
			.should(contentClause)
		);
		return NativeQuery.builder()
			.withQuery(boolQuery)
			.withPageable(PageRequest.of(newsSearch.getPage(), newsSearch.getSize()))
			.build();
	}

	/**
	 * NativeQuery를 실행하여 SearchHits 결과를 반환한다.
	 *
	 * @param query 실행할 NativeQuery 객체
	 * @return 검색된 SearchHits 결과
	 * @author 정안식
	 * @since 2025-05-15
	 */
	private SearchHits<NewsSearchDocument> executeSearch(NativeQuery query) {
		return ops.search(
			query,
			NewsSearchDocument.class,
			IndexCoordinates.of(indexName)
		);
	}

	/**
	 * SearchHits에서 도메인 결과로 매핑하여 리스트로 변환한다.
	 *
	 * @param hits Elasticsearch SearchHits 결과
	 * @return 매핑된 뉴스 도메인 결과 리스트
	 * @author 정안식
	 * @since 2025-05-15
	 */
	private List<NewsSearchResult> mapToDomain(SearchHits<NewsSearchDocument> hits) {
		return hits.get()
			.map(hit -> mapper.toDomain(hit.getContent()))
			.toList();
	}
}
