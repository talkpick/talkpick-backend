package com.likelion.backendplus4.talkpick.backend.search.presentation.controller.docs;

import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.request.NewsSearchRequest;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.request.NewsSimilarSearchRequest;
import com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.response.NewsSearchResponseList;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
	name = "News Search",
	description = "뉴스 검색 및 유사 뉴스 조회 API"
)
@RequestMapping("/public/news")
public interface NewsSearchControllerDocs {

	@Operation(
		summary = "뉴스 검색",
		description = "검색어 및 페이징 정보(NewsSearchRequest)를 기반으로 뉴스 검색 결과를 반환합니다."
	)
	ResponseEntity<ApiResponse<NewsSearchResponseList>> search(
		@Parameter(
			in = ParameterIn.QUERY,
			description = "검색어 및 페이지 정보",
			required = true
		)
		@ModelAttribute NewsSearchRequest request
	);

	@Operation(
		summary = "유사 뉴스 검색",
		description = "뉴스 ID 기반으로 유사한 뉴스를 조회하여 결과를 반환합니다."
	)
	ResponseEntity<ApiResponse<NewsSearchResponseList>> searchSimilar(
		@Parameter(
			in = ParameterIn.QUERY,
			description = "유사 뉴스 검색 요청 정보",
			required = true
		)
		@ModelAttribute NewsSimilarSearchRequest request
	);
}