package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.*;
import static com.likelion.backendplus4.talkpick.backend.news.info.presentation.mapper.NewsInfoDetailResponseMapper.*;
import static com.likelion.backendplus4.talkpick.backend.news.info.presentation.mapper.ScrapCommandMapper.*;


import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDynamic;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request.NewsInfoDynamicRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogJson;
import com.likelion.backendplus4.talkpick.backend.common.annotation.security.LoginUser;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request.ScrapRequest;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.docs.NewsInfoDetailProviderControllerDocs;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator.NewsIdConstraint;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Validated
@RestController
@RequiredArgsConstructor
public class NewsInfoDetailProviderController implements NewsInfoDetailProviderControllerDocs {
	private final NewsInfoDetailProviderUseCase newsInfoDetailProviderUseCase;
	private final NewsViewCountIncreaseUseCase newsViewCountIncreaseUseCase;

	/**
	 * 뉴스 ID를 기반으로 뉴스 상세 정보를 조회하는 API 엔드포인트입니다.
	 *
	 * 1. 뉴스 ID 형식 검증 (KM, DA, KH 접두사 + 숫자)
	 * 2. 클라이언트 IP 주소 획득
	 * 3. 조회수 증가 (중복 조회 제외)
	 * 4. 뉴스 상세 정보 조회 및 응답
	 *
	 * @param id 조회할 뉴스의 ID (형식: KM123, DA456, KH789)
	 * @return 뉴스 상세 정보가 포함된 API 응답
	 * @throws jakarta.validation.ConstraintViolationException 뉴스 ID 형식이 잘못된 경우
	 * @since 2025-05-19 최초 작성
	 * @author 양병학
	 * @modified 2025-05-25 양병학
	 *  - 뉴스 ID validation 추가
	 */
	@LogJson
	@EntryExitLog
	@Override
	@GetMapping("/public/news/{id}")
	public ResponseEntity<ApiResponse<NewsInfoDetailResponse>> getNewsInfoDetailsByArticleId(
		@PathVariable @NewsIdConstraint String id) {

		NewsInfoComplete newsInfoComplete = newsInfoDetailProviderUseCase.getNewsInfoDetailByNewsId(id);

		return success(toResponse(newsInfoComplete));
	}

	@LogJson
	@EntryExitLog
	@Override
	@PostMapping("/public/news/dynamic/{id}")
	public ResponseEntity<ApiResponse<NewsInfoDynamic>> getNewsInfoDynamic(
			@PathVariable @NewsIdConstraint String id,
			@Valid @RequestBody NewsInfoDynamicRequest request) {

		NewsInfoDynamic newsInfoDynamic = newsInfoDetailProviderUseCase.getNewsInfoDynamic(
				id,
				request.category(),
				request.publishDate()
		);

		return success(newsInfoDynamic);
	}

	@LogJson
	@EntryExitLog
	@Override
	@PostMapping("/scrap/{newsId}")
	public ResponseEntity<ApiResponse<Void>> saveScrap(
		@NotBlank(message = "newsId는 필수입니다.") @PathVariable String newsId,
		@LoginUser Long loginUser,
		@Valid @RequestBody ScrapRequest scrapRequest) {

		newsInfoDetailProviderUseCase.saveScrap(toCommand(newsId, loginUser, scrapRequest));

		return success();
	}
}