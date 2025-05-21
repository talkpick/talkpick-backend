package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageBrokerPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RabbitChatMessageBrokerAdapter implements ChatMessageBrokerPort {

	private final RabbitTemplate rabbitTemplate;

	/**
	 * 클라이언트로부터 수신된 채팅 메시지를 AMQP 교환기로 발행합니다.
	 *
	 * @param message 클라이언트에서 전송된 ChatMessage
	 * @author 이해창
	 * @since 2025-05-18
	 */
	@Override
	public void publishMessage(ChatMessage message) {
		rabbitTemplate.convertAndSend(
			"chat.exchange",
			"chat.article." + message.getArticleId(),
			message
		);
	}
}
