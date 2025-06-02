package com.likelion.backendplus4.talkpick.backend.search.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogMethodValues;
import com.likelion.backendplus4.talkpick.backend.search.application.port.in.NewsSearchUseCase;
import com.likelion.backendplus4.talkpick.backend.search.application.port.out.NewsSearchRepositoryPort;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearch;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResult;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResultAggregate;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSimilarSearch;

import lombok.RequiredArgsConstructor;

/**
 * 뉴스 검색 도메인 로직을 수행하는 서비스 구현체
 *
 * @since 2025-05-15
 */
@Service
@RequiredArgsConstructor
public class NewsSearchService implements NewsSearchUseCase {

	private final NewsSearchRepositoryPort repository;

	/**
	 * 도메인 모델을 기반으로 저장소에서 검색 결과를 조회하여 반환한다.
	 *
	 * @param newsSearch 검색어 및 페이징 정보를 담은 도메인 모델
	 * @return 검색된 뉴스 도메인 결과 리스트
	 * @author 정안식
	 * @since 2025-05-15
	 */
	@EntryExitLog
	@LogMethodValues
	@Override
	public NewsSearchResultAggregate searchByQuery(NewsSearch newsSearch) {
		return repository.searchByMatch(newsSearch);
	}

	@EntryExitLog
	@LogMethodValues
	@Override
	public List<NewsSearchResult> searchSimilarByNewsId(NewsSimilarSearch newsSimilarSearch) {
		return repository.searchByNewsId(newsSimilarSearch);
	}
}
