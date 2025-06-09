package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

import com.likelion.backendplus4.talkpick.backend.news.info.presentation.validator.NewsIdConstraint;

/**
 * 뉴스 조회수 요청 DTO
 *
 * @since 2025-06-08
 */
public record NewsInfoDynamicRequest(
		@NewsIdConstraint String newsId,
        @NotBlank String category,
        @NotNull
		LocalDateTime publishDate
) {}
