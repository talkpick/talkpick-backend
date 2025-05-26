package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

public interface ChatMessageBrokerPort {

	/**
	 * 클라이언트로부터 수신된 채팅 메시지를 AMQP 교환기로 발행합니다.
	 *
	 * @param message 클라이언트에서 전송된 ChatMessage
	 * @author 이해창
	 * @since 2025-05-18
	 */
	void publishMessage(ChatMessage message);
}
