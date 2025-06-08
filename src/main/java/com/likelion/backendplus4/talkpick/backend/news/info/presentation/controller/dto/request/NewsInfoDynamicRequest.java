package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDateTime;

public record NewsInfoDynamicRequest(
        @NotBlank String category,
        @NotNull LocalDateTime publishDate
) {}
