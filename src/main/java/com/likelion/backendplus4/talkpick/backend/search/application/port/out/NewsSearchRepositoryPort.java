package com.likelion.backendplus4.talkpick.backend.search.application.port.out;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearch;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResult;

/**
 * 뉴스 검색 저장소에 대한 포트 인터페이스
 *
 * @since 2025-05-15
 */
public interface NewsSearchRepositoryPort {
	/**
	 * 검색어에 매칭되는 뉴스 결과를 조회한다.
	 *
	 * @param newsSearch 검색어 및 페이징 정보 도메인 모델
	 * @return 뉴스 검색 도메인 결과 리스트
	 * @author 정안식
	 * @since 2025-05-15
	 */
	List<NewsSearchResult> searchByMatch(NewsSearch newsSearch);
}
