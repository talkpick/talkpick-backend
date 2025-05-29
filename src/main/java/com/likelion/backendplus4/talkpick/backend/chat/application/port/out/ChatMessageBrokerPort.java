package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

public interface ChatMessageBrokerPort {

	/**
	 * 주어진 기사(articleId)에 해당하는 라우팅 키로
	 * 채팅 메시지를 AMQP 교환기(RabbitMQ)로 발행합니다.
	 *
	 * @param articleId 기사 ID
	 * @param message 클라이언트에서 전송된 ChatMessage
	 * @author 이해창
	 * @since 2025-05-18
	 * @modified 2025.05.29 박찬병
	 * 2025.05.27 - 폴더 위치 이동
	 * 2025.05.29 - 메서드 이름 및 articleId 도 받아오도록 변경
	 */
	void deliverChatToArticle(String articleId, ChatMessage message);
}
