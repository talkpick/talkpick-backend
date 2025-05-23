package com.likelion.backendplus4.talkpick.backend.chat.application.service;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageBrokerPort;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageCachePort;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageDbPort;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageStreamPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.MessageType;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.support.mapper.ChatMessageResponseMapper;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements ChatUseCase {

    private static final int MAX_CACHE_SIZE = 100;

    private final ChatMessageBrokerPort brokerPort;
    private final ChatMessageCachePort cachePort;
    private final ChatMessageStreamPort streamPort;
    private final ChatMessageDbPort dbPort;

    @Override
    public SliceResponse<ChatMessageResponse> getChatMessage(String articleId) {
        List<ChatMessage> chatMessage = cachePort.getRecentMessages(articleId, MAX_CACHE_SIZE);
        if (chatMessage.isEmpty()) {
            chatMessage = dbPort.findRecentMessages(articleId, MAX_CACHE_SIZE);
            cachePort.cacheMessages(articleId, chatMessage);
        }
        List<ChatMessageResponse> result = chatMessage.stream()
            .map(ChatMessageResponseMapper::toResponseFromDomain)
            .toList();
        return new SliceResponse<>(result,true);
    }

    /**
     * 사용자가 채팅 메시지를 보낼 때 호출됩니다.
     * - 메시지 타입이 CHAT 인 경우
     *   - Redis 캐시에 저장
     *   - Redis 스트림에 저장
     * - RabbitMQ에 발행
     * @param message 채팅 메시지
     * @author 박찬병
     * @since 2025-05-20
     */
    @Override
    public void sendMessage(ChatMessage message) {
        if (message.getMessageType() == MessageType.CHAT) {
            cachePort.cache(message, MAX_CACHE_SIZE);
            streamPort.cacheToStream(message);
        }
        brokerPort.publishMessage(message);
    }
}
