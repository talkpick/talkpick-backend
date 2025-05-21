package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

public interface ChatSocketSenderPort {

	void sendToWebSocket(String articleId, ChatMessage message);
}
