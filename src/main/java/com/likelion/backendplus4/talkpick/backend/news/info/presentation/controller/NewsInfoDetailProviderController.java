package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/news/{id}")
public class NewsInfoDetailProviderController {
	private final NewsInfoDetailProviderUseCase newsInfoDetailProviderUseCase;
	private final NewsViewCountIncreaseUseCase newsViewCountIncreaseUseCase;

	@GetMapping
	public ResponseEntity<ApiResponse<NewsInfoDetailResponse>> getNewsInfoDetailsByArticleId(@PathVariable String id
		, HttpServletRequest request) {

		String ipAddress = request.getRemoteAddr();
		newsViewCountIncreaseUseCase.increaseViewCount(id, ipAddress);

		return success(newsInfoDetailProviderUseCase.getNewsInfoDetailByNewsId(id));
	}
}
