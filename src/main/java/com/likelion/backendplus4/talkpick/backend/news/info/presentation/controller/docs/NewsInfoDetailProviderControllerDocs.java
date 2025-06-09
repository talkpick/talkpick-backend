package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.docs;

import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDynamic;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request.NewsInfoDynamicRequest;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request.ScrapRequest;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator.NewsIdConstraint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
	name = "News Info Detail",
	description = "뉴스 상세 조회 및 스크랩 API"
)
public interface NewsInfoDetailProviderControllerDocs {

	@Operation(
		summary = "뉴스 상세 정보 조회",
		description = "뉴스 ID(@NewsIdConstraint)에 해당하는 상세 정보를 반환합니다."
	)
	ResponseEntity<ApiResponse<NewsInfoDetailResponse>> getNewsInfoDetailsByArticleId(
		@Parameter(
			in = ParameterIn.PATH,
			description = "조회할 뉴스 ID (형식: KM123, DA456, KH789)",
			required = true,
			example = "KM123"
		)
		@PathVariable @NewsIdConstraint String id
	);

	@Operation(
			summary = "뉴스 동적 정보 조회",
			description = "뉴스 ID에 해당하는 동적 정보(조회수 등)를 반환하고 조회수를 증가시킵니다."
	)
	ResponseEntity<ApiResponse<NewsInfoDynamic>> getNewsInfoDynamic(

			@Parameter(
					in = ParameterIn.DEFAULT,
					description = "동적 정보 조회를 위한 메타데이터",
					required = true
			)
			@Valid @RequestBody NewsInfoDynamicRequest request
	);

	@Operation(
		summary = "뉴스를 스크랩에 저장",
		description = "로그인된 사용자가 지정된 뉴스(@PathVariable newsId)를 스크랩합니다.",
		security = @SecurityRequirement(name = "BearerAuth")
	)
	ResponseEntity<ApiResponse<Void>> saveScrap(
		@Parameter(
			in = ParameterIn.PATH,
			description = "스크랩할 뉴스 ID",
			required = true,
			example = "KM123"
		)
		@NotBlank(message = "newsId는 필수입니다.") @PathVariable String newsId,
		@Parameter(
			in = ParameterIn.HEADER,
			description = "로그인된 사용자 ID (JWT로부터 추출됨)",
			hidden = true
		)
		Long loginUser,
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "스크랩 요청 DTO",
			required = true
		)
		@Valid @RequestBody ScrapRequest scrapRequest
	);
}
