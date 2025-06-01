package com.likelion.backendplus4.talkpick.backend.chat.application.service;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageBrokerPort;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageCachePort;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageDbPort;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageStreamPort;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatSessionPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.MessageType;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.support.mapper.SliceResponseChatMapper;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService implements ChatUseCase {

    private static final int DEFAULT_PAGE_NUMBER = 0;
    private static final int MAX_CACHE_SIZE = 100;
    private static final Set<MessageType> SAVABLE_TYPES = EnumSet.of(MessageType.CHAT, MessageType.QUOTE);

    private final ChatMessageBrokerPort brokerPort;
    private final ChatMessageCachePort cachePort;
    private final ChatMessageStreamPort streamPort;
    private final ChatSessionPort chatSessionPort;
    private final ChatMessageDbPort dbPort;

    /**
     * 캐시에서 최근 채팅 메시지를 조회하고, 메시지가 없으면 DB에서 로드하여 캐시에 저장한 뒤 반환합니다.
     *
     * @param articleId 게시글 식별자
     * @return 채팅 메시지 슬라이스 응답 (메시지 목록과 다음 페이지 여부)
     * @author 박찬병
     * @since 2025-05-25
     */
    @Override
    public SliceResponse<ChatMessageResponse> getChatMessage(String articleId) {
        List<ChatMessage> chatMessage = cachePort.getRecentMessages(articleId, MAX_CACHE_SIZE);

        if (chatMessage.isEmpty()) {
            chatMessage = loadFromDbAndCache(articleId);
        }

        boolean hasNext = cachePort.getHasNextFlag(articleId);
        return SliceResponseChatMapper.toSliceResponse(chatMessage, hasNext);
    }

    /**
     * 지정된 기준 ID(beforeId) 이전의 채팅 메시지를 DB에서 조회하여 반환합니다.
     *
     * @param articleId 게시글 식별자
     * @param before  이 시간 이전의 메시지들을 조회
     * @param limit     조회할 메시지 개수
     * @return 채팅 메시지 슬라이스 응답 (메시지 목록과 다음 페이지 여부)
     * @author 박찬병
     * @since 2025-05-25
     */
    @Override
    public SliceResponse<ChatMessageResponse> loadOlderMessages(String articleId, LocalDateTime before, int limit) {
        Slice<ChatMessage> chatMessage = dbPort.findBeforeMessages(articleId, before, PageRequest.of(DEFAULT_PAGE_NUMBER, limit));
        return SliceResponseChatMapper.toSliceResponse(chatMessage.stream().toList(), chatMessage.hasNext());
    }

    /**
     * 사용자가 채팅 메시지를 보낼 때 호출됩니다.
     * - 메시지 타입이 CHAT 인 경우
     *   - Redis 캐시에 저장
     *   - Redis 스트림에 저장
     * - RabbitMQ에 발행
     *
     * @param message 채팅 메시지
     * @author 박찬병
     * @since 2025-05-20
     * @modified 2025-05-30
     * 2025-05-29 내부 메서드 이름 변경
     * 2025-05-30 메시지 타입 추가
     */
    @Override
    public void sendMessage(ChatMessage message) {
        if (SAVABLE_TYPES.contains(message.getMessageType())) {
            cachePort.cache(message, MAX_CACHE_SIZE);
            streamPort.cacheToStream(message);
        }
        brokerPort.deliverChatToArticle(message.getArticleId(), message);
    }

    /**
     * 주어진 게시글 ID에 대한 초기 채팅 세션 수를 반환한다.
     *
     * @param articleId 게시글 식별자
     * @return 초기 채팅 세션 수
     * @author 이해창
     * @since 2025-05-24
     */
    @Override
    public int getInitialCount(String articleId) {
        return chatSessionPort.getSessionCount(articleId);
    }

    /**
     * DB에서 최근 채팅 메시지를 조회하고, 조회된 메시지를 캐시에 저장한 뒤 반환합니다.
     *
     * @param articleId 게시글 식별자
     * @return 조회된 채팅 메시지 리스트
     * @author 박찬병
     * @since 2025-05-25
     */
    private List<ChatMessage> loadFromDbAndCache(String articleId) {
        Slice<ChatMessage> slice = dbPort.findRecentMessages(articleId, PageRequest.of(DEFAULT_PAGE_NUMBER, MAX_CACHE_SIZE));
        List<ChatMessage> messages = slice.getContent();
        cachePort.cacheMessages(articleId, messages, slice.hasNext());
        return messages;
    }

}
