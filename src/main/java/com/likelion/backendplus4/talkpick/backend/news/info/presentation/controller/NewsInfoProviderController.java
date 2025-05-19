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

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news")
public class NewsInfoProviderController {
	private final NewsInfoProviderUseCase newsInfoProviderUsecase;

	@GetMapping("/latest")
	public ResponseEntity<ApiResponse<SliceResponse<NewsInfo>>> getLatestNewsInfo(@PathVariable String id) {
		return success(newsInfoDetailProviderUseCase.getNewsInfoDetailByNewsId(id));
	}
}
