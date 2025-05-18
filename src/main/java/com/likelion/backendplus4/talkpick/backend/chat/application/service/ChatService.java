package com.likelion.backendplus4.talkpick.backend.chat.application.service;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements ChatUseCase {
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate wsTemplate;

//    @Override
//    public void sendMessage(ChatMessage message) {
//        // 메시지를 처리하는 로직을 구현합니다.
//        log.debug("Sent User: {}", message.getSender());
//        log.debug("Sent message: {}", message.getContent());
//        log.debug("Sent ArticleId: {}", message.getArticleId());
//        log.debug("Sent time: {}", message.getTimestamp());
//
//
//    }

    /**
     * 클라이언트로부터 수신된 채팅 메시지를 AMQP 교환기로 발행합니다.
     *
     * @param message 클라이언트에서 전송된 ChatMessage
     * @author 이해창
     * @since 2025-05-18
     */
    @Override
    public void sendMessage(ChatMessage message) {
        log.debug("Received User: {}", message.getSender());
        log.debug("Received message: {}", message.getContent());
        log.debug("Received ArticleId: {}", message.getArticleId());
        log.debug("Received time: {}", message.getTimestamp());
        log.debug("Received type: {}", message.getMessageType());
        // AMQP 교환기에 메시지 발행 (Producer)
        rabbitTemplate.convertAndSend(
                "chat.exchange",
                "chat.article." + message.getArticleId(),
                message
        );
    }

    /**
     * RabbitMQ 큐로부터 메시지를 수신하여 WebSocket 클라이언트로 브로드캐스트합니다.
     *
     * @param message RabbitListener가 수신한 ChatMessage
     *
     * @author 이해창
     * @since  2025-05-18
     */
    @Override
    @RabbitListener(queues = "chat.queue.default")
    public void receiveMessage(ChatMessage message) {
        String destination = "/topic/chat." + message.getArticleId();
        wsTemplate.convertAndSend(destination, message);
    }

//    @Override
//    public void subscribe(SessionSubscribeEvent event) {
//        log.debug("Received subscribe event: {}", event);
//        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
//        String destination = accessor.getDestination();
//
//    }
}
