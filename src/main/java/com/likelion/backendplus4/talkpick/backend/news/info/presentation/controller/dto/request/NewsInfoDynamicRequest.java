package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * 뉴스 조회수 요청 DTO
 *
 * @since 2025-06-08
 */
public record NewsInfoDynamicRequest(
        @NotBlank String category,
        @NotNull LocalDateTime publishDate
) {}
