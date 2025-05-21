package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.support.mapper.ChatMessageRequestMapper;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.request.ChatMessageRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatUseCase chatUseCase;

    /**
     * 클라이언트가 '/app/chat.send'로 전송한 ChatMessage를 처리합니다.
     * @param request 클라이언트로부터 수신한 메시지
     * @author 이해창
     * @since 2025-05-18
     */
    @MessageMapping("/chat.send")
    public void handleChatSend(@Payload ChatMessageRequest request) {
        chatUseCase.sendMessage(ChatMessageRequestMapper.toDomain(request));
    }

}
