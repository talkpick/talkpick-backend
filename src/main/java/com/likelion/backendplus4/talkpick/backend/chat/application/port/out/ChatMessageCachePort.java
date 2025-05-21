package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

public interface ChatMessageCachePort {
	void cache(ChatMessage message);
	List<ChatMessage> getRecentMessages(String articleId);
}
