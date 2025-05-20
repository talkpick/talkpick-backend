package com.likelion.backendplus4.talkpick.backend.search.presentation.controller.dto.request;

import org.springframework.web.bind.annotation.RequestParam;

public record NewsSimilarSearchRequest(
	@RequestParam("newsId") String newsId,
	@RequestParam(value = "page", defaultValue = "0") int page,
	@RequestParam(value = "size", defaultValue = "10") int size
) {}