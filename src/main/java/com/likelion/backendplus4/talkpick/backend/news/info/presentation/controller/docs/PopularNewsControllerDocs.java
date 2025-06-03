package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.docs;

import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(
	name = "Popular News",
	description = "카테고리별 Top1 인기뉴스 조회 API"
)
public interface PopularNewsControllerDocs {

	@Operation(
		summary = "카테고리별 Top1 인기뉴스 조회",
		description = "영어 카테고리명을 입력받아 해당 카테고리의 Top1 인기뉴스를 반환합니다. " +
			"예: /top-viewed/politics → 정치 카테고리 조회"
	)
	ResponseEntity<ApiResponse<PopularNewsResponse>> getTopViewedNewsByCategory(
		@Parameter(
			in = ParameterIn.PATH,
			description = "영어 카테고리명 (예: politics, society, economy, sports, international, entertainment, all)",
			required = true,
			example = "sports"
		)
		String category
	);
}
