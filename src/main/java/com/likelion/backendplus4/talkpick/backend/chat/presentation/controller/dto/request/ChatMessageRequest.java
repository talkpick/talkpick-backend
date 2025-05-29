package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.request;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.MessageType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessageRequest {
    private final String articleId;
    private final String sender;
    private final String content;
    private final String messageType;
}
