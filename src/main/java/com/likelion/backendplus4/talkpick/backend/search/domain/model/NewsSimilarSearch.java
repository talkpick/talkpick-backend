package com.likelion.backendplus4.talkpick.backend.search.domain.model;

import com.likelion.backendplus4.talkpick.backend.search.exception.SearchException;
import com.likelion.backendplus4.talkpick.backend.search.exception.error.SearchErrorCode;

import lombok.Getter;

/**
 * 뉴스 검색 시 전달되는 검색어와 페이징 정보를 검증 및 저장하는 도메인 모델
 *
 * @since 2025-05-15
 */
@Getter
public class NewsSimilarSearch {
	private final String newsId;
	private final int page;
	private final int size;

	/**
	 * 뉴스아이디, 페이지, 사이즈 유효성을 검증하면서 인스턴스를 생성한다.
	 *
	 * @param newsId 검색할 뉴스아이디
	 * @param page  조회할 페이지 번호 (0 이상)
	 * @param size  페이지당 결과 개수 (1 이상)
	 * @throws SearchException 유효하지 않은 파라미터일 경우
	 * @author 정안식
	 * @since 2025-05-15
	 */
	public NewsSimilarSearch(String newsId, int page, int size) {
		if (newsId == null || newsId.isBlank()) {
			throw new SearchException(SearchErrorCode.INVALID_NEWS_ID);
		}
		if (page < 0) {
			throw new SearchException(SearchErrorCode.INVALID_PAGE);
		}
		if (size <= 0) {
			throw new SearchException(SearchErrorCode.INVALID_SIZE);
		}
		this.newsId = newsId;
		this.page = page;
		this.size = size;
	}
}
