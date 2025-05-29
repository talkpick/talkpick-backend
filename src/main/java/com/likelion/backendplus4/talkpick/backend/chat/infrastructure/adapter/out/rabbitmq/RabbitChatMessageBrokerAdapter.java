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
	@Override
	public void deliverChatToArticle(String articleId, ChatMessage message) {
		ChatMessageResponse chatResponse = ChatMessageResponseMapper.toResponseFromDomain(message);
		rabbitTemplate.convertAndSend(
			CHAT_EXCHANGE,
			ARTICLE_ROUTING_KEY + articleId,
			chatResponse
		);
	}
}
