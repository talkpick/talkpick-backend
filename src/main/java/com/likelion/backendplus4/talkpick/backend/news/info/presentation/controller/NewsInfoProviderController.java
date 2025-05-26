package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/news")
public class NewsInfoProviderController {
	private final NewsInfoProviderUseCase newsInfoProviderUsecase;

	@GetMapping("/latest")
	public ResponseEntity<ApiResponse<SliceResponse>> getLatestNewsInfo(
		NewsInfoRequest newsInfoRequest) {
		SliceResult<NewsInfo> latestNewsInfos =
			newsInfoProviderUsecase.getLatestNewsInfo(newsInfoRequest.lastId(), newsInfoRequest.size());

		return success(NewsInfoResponseMapper.toSliceResponse(latestNewsInfos));
	}

	@GetMapping("/latest/category")
	public ResponseEntity<ApiResponse<SliceResponse>> getLatestNewsInfoByCategory(
		NewsInfoRequestByCategory newsInfoRequestByCategory) {
		SliceResult<NewsInfo> latestNewsInfos =
			newsInfoProviderUsecase.getLatestNewsInfoByCategory(
				newsInfoRequestByCategory.category(),
				newsInfoRequestByCategory.lastId(),
				newsInfoRequestByCategory.size());

		return success(NewsInfoResponseMapper.toSliceResponse(latestNewsInfos));
	}
}
