package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.support.mapper.ChatMessageRequestMapper;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.request.ChatMessageRequest;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatCountResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final ChatUseCase chatUseCase;

    /**
     * 클라이언트가 '/app/chat.send'로 전송한 ChatMessage를 처리합니다.
     *
     * @param request 클라이언트로부터 수신한 메시지
     * @author 이해창
     * @since 2025-05-18
     */
    @MessageMapping("/chat.send")
    public void handleChatSend(@Payload ChatMessageRequest request) {
        chatUseCase.sendMessage(ChatMessageRequestMapper.toDomain(request));
    }

    /**
     * 초기 채팅방 접속 인원 수를 반환한다.
     * <p>
     * 클라이언트가 특정 채팅방의 인원 수를 구독후 호출되며, 현재 접속 중인 세션 수를 구독자에 전송한다.
     * </p>
     *
     * @param articleId 채팅 대상 뉴스 식별자
     * @author 이해창
     * @since 2025-05-26
     * @modified 2025-06-01 이해창
     * - SubscribeMapping -> MessageMapping으로 변경 및 요청 엔드포인트 변경
     */
    @MessageMapping("/chat.initCount.{articleId}")
    public void userCount(@DestinationVariable String articleId) {
        chatUseCase.getInitialCount(articleId);
    }

}
