package com.likelion.backendplus4.talkpick.backend.search.application.port.in.mapper;

import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearch;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSimilarSearch;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.request.NewsSearchRequest;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.request.NewsSimilarSearchRequest;

/**
 * 요청 DTO를 도메인 모델로 변환하는 매퍼 클래스
 *
 * @since 2025-05-15
 */
@Component
public class NewsSearchRequestMapper {
	/**
	 * 요청 DTO를 NewsSearch 도메인 모델로 변환한다.
	 *
	 * @param req 변환할 요청 DTO
	 * @return 생성된 도메인 모델 객체
	 * @author 정안식
	 * @since 2025-05-15
	 */
	public static NewsSearch toDomain(NewsSearchRequest req) {
		return new NewsSearch(req.q(), req.page(), req.size());
	}

	public static NewsSimilarSearch toDomain(NewsSimilarSearchRequest req) {
		return new NewsSimilarSearch(req.newsId(), req.page(), req.size());
	}
}
