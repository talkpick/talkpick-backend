package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.support.mapper;

import java.time.format.DateTimeFormatter;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;

public class ChatMessageResponseMapper {

	private static final DateTimeFormatter ISO_FORMATTER =
		DateTimeFormatter.ISO_LOCAL_DATE_TIME;

	public static ChatMessageResponse toResponseFromDomain(ChatMessage message) {
		return ChatMessageResponse.builder()
			.articleId(message.getArticleId())
			.content(message.getContent())
			.sender(message.getSender())
			.timestamp(message.getTimestamp().format(ISO_FORMATTER))
			.build();
	}
}
