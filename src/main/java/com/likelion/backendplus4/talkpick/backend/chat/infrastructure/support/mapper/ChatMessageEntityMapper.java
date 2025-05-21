package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.entity.ChatMessageEntity;

public class ChatMessageEntityMapper {

	public static ChatMessageEntity toEntityFromDomain(ChatMessage chatMessage) {
		return ChatMessageEntity.builder()
			.articleId(chatMessage.getArticleId())
			.sender(chatMessage.getSender())
			.content(chatMessage.getContent())
			.timestamp(chatMessage.getTimestamp())
			.build();
	}
}
