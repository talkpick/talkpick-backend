package com.likelion.backendplus4.talkpick.backend.search.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.*;
import static com.likelion.backendplus4.talkpick.backend.search.application.port.in.mapper.NewsSearchRequestMapper.*;
import static com.likelion.backendplus4.talkpick.backend.search.application.port.in.mapper.NewsSearchResponseMapper.*;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogJson;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.search.application.port.in.NewsSearchUseCase;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearch;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResult;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSearchResultAggregate;
import com.likelion.backendplus4.talkpick.backend.search.domain.model.NewsSimilarSearch;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.docs.NewsSearchControllerDocs;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.request.NewsSearchRequest;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.request.NewsSimilarSearchRequest;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.response.NewsSearchResponseList;

import lombok.RequiredArgsConstructor;

/**
 * 뉴스 검색 기능을 제공하는 REST 컨트롤러
 *
 * @since 2025-05-15
 */
@RestController
@RequestMapping("/public/news")
@RequiredArgsConstructor
public class NewsSearchController implements NewsSearchControllerDocs {

	private final NewsSearchUseCase searchUseCase;

	/**
	 * 검색 요청에 따라 뉴스를 조회하고 결과를 반환한다.
	 *
	 * @param request 검색어 및 페이지 정보가 담긴 요청 DTO
	 * @return ApiResponse에 래핑된 검색 결과 리스트
	 * @author 정안식
	 * @since 2025-05-15
	 */
	@LogJson
	@EntryExitLog
	@Override
	@GetMapping("/search")
	public ResponseEntity<ApiResponse<NewsSearchResponseList>> search(
		@ModelAttribute NewsSearchRequest request) {
		NewsSearch newsSearch = toDomain(request);
		NewsSearchResultAggregate newsSearchResultAggregate = searchUseCase.searchByQuery(newsSearch);

		return success(toListResponse(newsSearchResultAggregate));
	}

	@LogJson
	@EntryExitLog
	@Override
	@GetMapping("/similar")
	public ResponseEntity<ApiResponse<NewsSearchResponseList>> searchSimilar(
		@ModelAttribute NewsSimilarSearchRequest request) {
		NewsSimilarSearch newsSimilarSearch = toDomain(request);
		List<NewsSearchResult> newsSearchResultList = searchUseCase.searchSimilarByNewsId(newsSimilarSearch);

		return success(toListResponse(newsSearchResultList));
	}
}
