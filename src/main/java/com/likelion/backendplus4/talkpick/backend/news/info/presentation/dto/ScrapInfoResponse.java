package com.likelion.backendplus4.talkpick.backend.news.info.presentation.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScrapInfoResponse(
	Integer id,
	String snippetText,
	int startOffset,
	int endOffset,
	LocalDateTime createdAt
) {}
