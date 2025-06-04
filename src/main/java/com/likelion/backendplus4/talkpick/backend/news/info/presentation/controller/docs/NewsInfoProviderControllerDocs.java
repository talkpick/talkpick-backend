package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.docs;


import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request.NewsInfoRequest;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request.NewsInfoRequestByCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
	name = "News Info Provider",
	description = "최신 뉴스 및 카테고리별 최신 뉴스 조회 API"
)
@RequestMapping("/public/news")
public interface NewsInfoProviderControllerDocs {

	@Operation(
		summary = "최신 뉴스 조회",
		description = "마지막 뉴스 ID와 조회 개수(NewsInfoRequest)를 기반으로 최신 뉴스를 페이징 조회합니다."
	)
	ResponseEntity<ApiResponse<SliceResponse>> getLatestNewsInfo(
		@Parameter(
			in = ParameterIn.QUERY,
			description = "마지막 뉴스 ID 및 조회 개수를 포함한 요청 파라미터",
			required = true
		)
		@ModelAttribute NewsInfoRequest newsInfoRequest
	);

	@Operation(
		summary = "카테고리별 최신 뉴스 조회",
		description = "카테고리, 마지막 뉴스 ID, 조회 개수(NewsInfoRequestByCategory)를 기반으로 해당 카테고리의 최신 뉴스를 페이징 조회합니다."
	)
	ResponseEntity<ApiResponse<SliceResponse>> getLatestNewsInfoByCategory(
		@Parameter(
			in = ParameterIn.PATH,
			description = "뉴스 카테고리",
			required = true,
			example = "sports"
		)
		@NotBlank @PathVariable String category,
		@Parameter(
			in = ParameterIn.QUERY,
			description = "마지막 뉴스 ID 및 조회 개수를 포함한 요청 파라미터",
			required = true
		)
		@ModelAttribute NewsInfoRequestByCategory newsInfoRequestByCategory
	);
}