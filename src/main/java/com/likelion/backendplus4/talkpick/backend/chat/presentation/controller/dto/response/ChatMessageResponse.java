package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response;

import lombok.Builder;

@Builder
public record ChatMessageResponse(
	String sender,
	String content,
	String timestamp
) {
}
