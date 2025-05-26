package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.in.rabbitmq;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatSocketSenderPort;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RabbitMessageListener {

	private final ChatSocketSenderPort socketSenderPort;

	/**
	 * RabbitMQ 큐로부터 메시지를 수신하여 브로드캐스트합니다.
	 *
	 * @param message RabbitListener가 수신한 ChatMessage
	 * @author 이해창
	 * @since 2025-05-18
	 * @modified 2025-05-20
	 * 2025-05-20 핵사고날 구조에 맞게 분리
	 */
	@RabbitListener(queues = "chat.queue.default")
	public void receiveMessage(ChatMessageResponse message) {
		socketSenderPort.sendToWebSocket(message.articleId(), message);
	}

}
