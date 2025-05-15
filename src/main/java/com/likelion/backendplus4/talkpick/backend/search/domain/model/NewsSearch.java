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
public class NewsSearch {
	private final String query;
	private final int page;
	private final int size;

	/**
	 * 검색어, 페이지, 사이즈 유효성을 검증하면서 인스턴스를 생성한다.
	 *
	 * @param query 검색어 문자열
	 * @param page  조회할 페이지 번호 (0 이상)
	 * @param size  페이지당 결과 개수 (1 이상)
	 * @throws SearchException 유효하지 않은 파라미터일 경우
	 * @author 정안식
	 * @since 2025-05-15
	 */
	public NewsSearch(String query, int page, int size) {
		if (query == null || query.isBlank()) {
			throw new SearchException(SearchErrorCode.INVALID_QUERY);
		}
		if (page < 0) {
			throw new SearchException(SearchErrorCode.INVALID_PAGE);
		}
		if (size <= 0) {
			throw new SearchException(SearchErrorCode.INVALID_SIZE);
		}
		this.query = query;
		this.page = page;
		this.size = size;
	}
}
