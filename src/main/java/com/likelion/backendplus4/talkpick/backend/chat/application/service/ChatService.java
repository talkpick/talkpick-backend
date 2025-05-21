package com.likelion.backendplus4.talkpick.backend.chat.application.service;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageBrokerPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements ChatUseCase {

    private final ChatMessageBrokerPort brokerPort;

    /**
     * 사용자가 채팅 메시지를 보낼 때 호출됩니다.
     * - RabbitMQ에 발행
     * @param message 채팅 메시지
     * @author 박찬병
     * @since 2025-05-20
     */
    @Override
    public void sendMessage(ChatMessage message) {
        brokerPort.publishMessage(message);
    }
}
