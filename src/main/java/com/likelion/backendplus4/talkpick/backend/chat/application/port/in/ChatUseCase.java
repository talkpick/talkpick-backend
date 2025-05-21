package com.likelion.backendplus4.talkpick.backend.chat.application.port.in;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;

import org.springframework.web.socket.messaging.SessionSubscribeEvent;

public interface ChatUseCase {

    void sendMessage(ChatMessage message);
    SliceResponse<ChatMessageResponse> getChatMessage(String articleId);

}
