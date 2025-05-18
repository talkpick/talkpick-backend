package com.likelion.backendplus4.talkpick.backend.chat.application.service;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.MessageType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.security.Principal;
import java.time.LocalDateTime;

import static com.likelion.backendplus4.talkpick.backend.chat.config.RabbitMQConfig.FORMATTER;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements ChatUseCase {
    private final RabbitTemplate rabbitTemplate;
    private final SimpMessagingTemplate wsTemplate;

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
                "chat.exchange",
                "chat.article." + message.getArticleId(),
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
    @RabbitListener(queues = "chat.queue.default")
    public void receiveMessage(ChatMessage message) {
        String destination = "/topic/chat." + message.getArticleId();
        wsTemplate.convertAndSend(destination, message);
    }


    @Override
    public void subscribe(SessionSubscribeEvent event) {
        log.debug("Received subscribe event: {}", event);
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        if (isChat(destination)) {
            String articleId = getArticleId(destination);
            String sender = getNickname(accessor);
            ChatMessage joinMessage = buildJoinMessage(articleId, sender);
            rabbitTemplate.convertAndSend(
                    "chat.exchange",
                    "chat.article." + joinMessage.getArticleId(),
                    joinMessage
            );
        }
    }

    private boolean isChat(String destination) {
        return destination != null && destination.startsWith("/topic/chat.");
    }

    private String getNickname(StompHeaderAccessor accessor) {
        Principal user = accessor.getUser();
        return (user != null) ? user.getName() : "Anonymous";
    }

    private String getArticleId(String destination) {
        return destination.substring(destination.lastIndexOf('.') + 1);
    }

    private ChatMessage buildJoinMessage(String articleId, String sender) {
        return ChatMessage.builder()
                .articleId(articleId)
                .sender("SYSTEM")
                .content(sender + "님이 입장하셨습니다.")
                .timestamp(LocalDateTime.now().format(FORMATTER))
                .messageType(MessageType.JOIN)
                .build();
    }
}
