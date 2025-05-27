package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageBrokerPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.support.mapper.ChatMessageResponseMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RabbitChatMessageBrokerAdapter implements ChatMessageBrokerPort {

	private static final String CHAT_EXCHANGE = "chat.exchange";
	private static final String ARTICLE_ROUTING_KEY = "chat.article.";

	private final RabbitTemplate rabbitTemplate;

	/**
	 * 클라이언트로부터 수신된 채팅 메시지를 AMQP 교환기로 발행합니다.
	 *
	 * @param message 클라이언트에서 전송된 ChatMessage
	 * @author 이해창
	 * @since 2025-05-18
	 * @modified 2025.05.27 박찬병
	 * 2025.05.27 - 폴더 위치 이동
	 */
	@Override
	public void publishMessage(ChatMessage message) {
		ChatMessageResponse chatResponse = ChatMessageResponseMapper.toResponseFromDomain(message);
		rabbitTemplate.convertAndSend(
			CHAT_EXCHANGE,
			ARTICLE_ROUTING_KEY + chatResponse.articleId(),
			chatResponse
		);
	}
}
