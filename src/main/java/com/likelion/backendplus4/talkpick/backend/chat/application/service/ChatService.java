package com.likelion.backendplus4.talkpick.backend.chat.application.service;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatSessionPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements ChatUseCase {
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate wsTemplate;
    private final ChatSessionPort chatSessionPort;

    private static final String CHAT_EXCHANGE = "chat.exchange";
    private static final String ARTICLE_ROUTING_KEY = "chat.article.";
    private static final String CHAT_QUEUE = "chat.queue.default";
    private static final String CHAT_TOPIC_PREFIX = "/topic/chat.";

    /**
     * 클라이언트로부터 수신된 채팅 메시지를 AMQP 교환기로 발행합니다.
     *
     * @param message 클라이언트에서 전송된 ChatMessage
     * @author 이해창
     * @since 2025-05-18
     */
    @Override
    public void sendMessage(ChatMessage message) {
        rabbitTemplate.convertAndSend(
                CHAT_EXCHANGE,
                ARTICLE_ROUTING_KEY + message.getArticleId(),
                message
        );
    }

    /**
     * RabbitMQ 큐로부터 메시지를 수신하여 WebSocket 클라이언트로 브로드캐스트합니다.
     *
     * @param message RabbitListener가 수신한 ChatMessage
     * @author 이해창
     * @since 2025-05-18
     */
    @Override
    @RabbitListener(queues = CHAT_QUEUE)
    public void receiveMessage(ChatMessage message) {
        String destination = CHAT_TOPIC_PREFIX + message.getArticleId();
        wsTemplate.convertAndSend(destination, message);
    }

    @Override
    public int getInitialCount(String articleId) {
        return chatSessionPort.getSessionCount(articleId);
    }
}
