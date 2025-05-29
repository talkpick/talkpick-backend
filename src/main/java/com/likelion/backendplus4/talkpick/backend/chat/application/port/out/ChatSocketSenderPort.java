package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;

public interface ChatSocketSenderPort {

	/**
	 * 수신한 메시지를 WebSocket 클라이언트로 브로드캐스트합니다.
	 *
	 * @param message RabbitListener가 수신한 ChatMessage
	 * @author 이해창
	 * @since 2025-05-18
	 * @modified 2025-05-20
	 */
	void sendToWebSocket(String articleId, ChatMessageResponse message);
}
