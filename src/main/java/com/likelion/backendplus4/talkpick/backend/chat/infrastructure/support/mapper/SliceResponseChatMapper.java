package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.support.mapper;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.support.mapper.ChatMessageResponseMapper;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;

public class SliceResponseChatMapper {

	public static SliceResponse<ChatMessageResponse> toSliceResponse(List<ChatMessage> chatMessages, boolean hasNext) {
		List<ChatMessageResponse> result = chatMessages
			.stream()
			.map(ChatMessageResponseMapper::toResponseFromDomain)
			.toList();
		return new SliceResponse<>(result, hasNext);
	}
}
