package com.likelion.backendplus4.talkpick.backend.search.application.port.in;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearch;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResult;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSimilarSearch;

/**
 * 뉴스 검색 유스케이스 포트 인터페이스
 *
 * @author 정안식
 * @since 2025-05-15
 */
public interface NewsSearchUseCase {
	/**
	 * 도메인 모델을 기반으로 검색 결과를 조회한다.
	 *
	 * @param newsSearch 검색어 및 페이징 정보 도메인 모델
	 * @return 뉴스 검색 도메인 결과 리스트
	 * @author 정안식
	 * @since 2025-05-15
	 */
	List<NewsSearchResult> searchByQuery(NewsSearch newsSearch);

	List<NewsSearchResult> searchSimilarByNewsId(NewsSimilarSearch newsSimilarSearch);
}
