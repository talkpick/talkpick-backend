package com.likelion.backendplus4.talkpick.backend.search.infrastructure.adapter;

import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.*;

import java.util.ArrayList;
import static co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.functionScore;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResultAggregate;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSimilarSearch;
import com.likelion.backendplus4.talkpick.backend.search.exception.SearchException;
import com.likelion.backendplus4.talkpick.backend.search.exception.error.SearchErrorCode;
import com.likelion.backendplus4.talkpick.backend.search.infrastructure.adapter.document.NewsSearchDocument;
import com.likelion.backendplus4.talkpick.backend.search.infrastructure.adapter.mapper.NewsSearchDocumentMapper;

import co.elastic.clients.elasticsearch._types.query_dsl.FunctionScoreMode;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;

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
	public NewsSearchResultAggregate searchByMatch(NewsSearch newsSearch) {
		NativeQuery query = buildNativeQuery(newsSearch);
		SearchHits<NewsSearchDocument> hits = executeSearch(query);
		long totalHits = hits.getTotalHits();

		return mapToDomain(hits, totalHits);
	}

	@EntryExitLog
	@LogMethodValues
	@Override
	public List<NewsSearchResult> searchByNewsId(NewsSimilarSearch newsSimilarSearch) {
		NewsSearchDocument origin = fetchOriginalDocument(newsSimilarSearch.getNewsId());
		float[] queryVector = origin.getSummaryVector();

		NativeQuery cosineQuery = buildCosineQuery(queryVector, newsSimilarSearch);
		SearchHits<NewsSearchDocument> hits = executeSearch(cosineQuery);


		return mapToDomain(hits);
	}

	private NewsSearchDocument fetchOriginalDocument(String newsId) {
		NewsSearchDocument doc = ops.get(
			newsId,
			NewsSearchDocument.class,
			IndexCoordinates.of(indexName)
		);
		if (doc == null) {
			throw new SearchException(SearchErrorCode.INVALID_NEWS_ID);
		}
		return doc;
	}

	private NativeQuery buildCosineQuery(float[] queryVector, NewsSimilarSearch search) {
		// 1) float[] → List<Float> 로 변환
		List<Float> vectorList = new ArrayList<>(queryVector.length);
		for (float v : queryVector) {
			vectorList.add(v);
		}

		// 2) JsonData 파라미터 준비
		Map<String, JsonData> params = Map.of(
			"query_vector", JsonData.of(vectorList)
		);

		// 3) functionScore 로 Query 객체 생성
		Query cosineScoreQuery = functionScore(fs -> fs
			.functions(fn -> fn
				.scriptScore(sc -> sc.script(s -> s
					.source("cosineSimilarity(params.query_vector, 'summaryVector') + 1.0")
					.params(params)
				))
			)
			.scoreMode(FunctionScoreMode.Sum)
		);

		// 4) 완성된 Query를 withQuery()에 직접 전달
		return NativeQuery.builder()
			.withQuery(cosineScoreQuery)
			.withPageable(PageRequest.of(search.getPage(), search.getSize()))
			.build();
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
	private NewsSearchResultAggregate mapToDomain(SearchHits<NewsSearchDocument> hits, long totalHits) {
		List<NewsSearchResult> resultList = hits.get()
			.map(hit -> mapper.toDomain(hit.getContent()))
			.toList();

		return NewsSearchResultAggregate.builder()
			.newsSearchResultList(resultList)
			.totalHits(totalHits)
			.build();
	}

	private List<NewsSearchResult> mapToDomain(SearchHits<NewsSearchDocument> hits) {
		return hits.get()
			.map(hit -> mapper.toDomain(hit.getContent()))
			.toList();
	}
}
