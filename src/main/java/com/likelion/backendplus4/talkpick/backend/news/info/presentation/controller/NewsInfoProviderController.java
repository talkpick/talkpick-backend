package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.NewsInfoRequest;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.NewsInfoRequestByCategory;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.mapper.NewsInfoResponseMapper;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

/**
 * 뉴스 정보를 조회하는 공개 API 컨트롤러
 * 최신 뉴스 및 카테고리별 최신 뉴스 리스트를 제공한다.
 *
 * @since 2025-05-16
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/news")
public class NewsInfoProviderController {
	private final NewsInfoProviderUseCase newsInfoProviderUsecase;

	/**
	 * 최신 뉴스 정보를 조회합니다.
	 *
	 * @param newsInfoRequest 마지막 뉴스 ID 및 조회 개수를 포함한 요청 파라미터
	 * @return 뉴스 정보 목록을 포함한 응답
	 * @author 함예정
	 * @since 2025-05-16
	 */
	@GetMapping("/latest")
	public ResponseEntity<ApiResponse<SliceResponse>> getLatestNewsInfo(
		NewsInfoRequest newsInfoRequest) {
		SliceResult<NewsInfo> latestNewsInfos =
			newsInfoProviderUsecase.getLatestNewsInfo(newsInfoRequest.lastId(), newsInfoRequest.size());

		return success(NewsInfoResponseMapper.toSliceResponse(latestNewsInfos));
	}

	/**
	 * 특정 카테고리의 최신 뉴스 정보를 조회합니다.
	 *
	 * @param newsInfoRequestByCategory 카테고리, 마지막 뉴스 ID, 조회 개수를 포함한 요청 파라미터
	 * @return 카테고리별 뉴스 정보 목록을 포함한 응답
	 * @author 함예정
	 * @since 2025-05-26
	 */
	@GetMapping("/latest/{category}")
	public ResponseEntity<ApiResponse<SliceResponse>> getLatestNewsInfoByCategory(
		@NotBlank @PathVariable String category,
		NewsInfoRequestByCategory newsInfoRequestByCategory) {
		SliceResult<NewsInfo> latestNewsInfos =
			newsInfoProviderUsecase.getLatestNewsInfoByCategory(
				category,
				newsInfoRequestByCategory.lastId(),
				newsInfoRequestByCategory.size());

		return success(NewsInfoResponseMapper.toSliceResponse(latestNewsInfos));
	}
}
