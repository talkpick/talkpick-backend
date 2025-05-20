package com.likelion.backendplus4.talkpick.backend.chat.application.port.in.mapper;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.MessageType;
import com.likelion.backendplus4.talkpick.backend.chat.exception.ChatException;
import com.likelion.backendplus4.talkpick.backend.chat.exception.error.ChatErrorCode;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.request.ChatMessageRequest;

/**
 * 요청 DTO를 도메인 모델로 변환하는 매퍼 클래스
 *
 * @since 2025-05-18
 */
public class ChatMessageRequestMapper {
    public static ChatMessage toDomain(ChatMessageRequest request) {
        return new ChatMessage(
                request.getArticleId(),
                request.getSender(),
                request.getContent(),
                request.getTimestamp(),
                stringToMessageType(request.getMessageType())
        );
    }

    private static MessageType stringToMessageType(String type) {
        return switch (type.toUpperCase()) {
            case "CHAT" -> MessageType.CHAT;
            case "JOIN" -> MessageType.JOIN;
            case "LEAVE" -> MessageType.LEAVE;
            default -> throw new ChatException(ChatErrorCode.INVALID_MESSAGE_TYPE);
        };
    }
}
