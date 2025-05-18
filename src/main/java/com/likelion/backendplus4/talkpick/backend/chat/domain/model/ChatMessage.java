package com.likelion.backendplus4.talkpick.backend.chat.domain.model;

import com.likelion.backendplus4.talkpick.backend.chat.exception.ChatException;
import com.likelion.backendplus4.talkpick.backend.chat.exception.error.ChatErrorCode;
import lombok.Getter;


@Getter
public class ChatMessage {
    private final String articleId;
    private final String sender;
    private final String content;
    private final String timestamp;
    private final MessageType messageType;

    public ChatMessage(String articleId, String sender, String content, String timestamp, MessageType messageType) {
        if(articleId == null || articleId.isEmpty()) {
            throw new ChatException(ChatErrorCode.INVALID_ARTICLE_ID);
        }
        if(sender == null || sender.isEmpty()) {
            throw new ChatException(ChatErrorCode.INVALID_SENDER);
        }

        this.articleId = articleId;
        this.sender = sender;
        this.content = content;
        this.timestamp = timestamp;
        this.messageType = messageType;
    }
}