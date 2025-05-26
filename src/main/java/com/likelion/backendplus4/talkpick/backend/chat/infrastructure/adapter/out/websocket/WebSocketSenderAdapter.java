package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.websocket;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatSocketSenderPort;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketSenderAdapter implements ChatSocketSenderPort {

	private final SimpMessagingTemplate wsTemplate;

	/**
	 * 수신한 메시지를 WebSocket 클라이언트로 브로드캐스트합니다.
	 *
	 * @param message RabbitListener가 수신한 ChatMessage
	 * @author 이해창
	 * @since 2025-05-18
	 * @modified 2025-05-20
	 */
	@Override
	public void sendToWebSocket(String articleId, ChatMessageResponse message) {
		String destination = "/topic/chat." + articleId;
		wsTemplate.convertAndSend(destination, message);
	}
}
