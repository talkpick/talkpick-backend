package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.eventlistener;


import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatSessionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;

import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;


/**
 * 웹소켓 세션 이벤트를 수신하고 세션 정보를 ChatSessionPort로 전달하는 이벤트 리스너 클래스
 *
 * @since 2025-05-26
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ChatSessionEventListener {

    private final ChatSessionPort chatSessionPort;

    /**
     * 사용자의 웹소켓 구독 요청을 처리하고 세션 정보를 등록한다.
     *
     * @param event 세션 구독 이벤트
     * @author 이해창
     * @since 2025-05-26
     * @modified 2025-06-03 이해창
     *  - 채팅방 구독 시 카테고리 정보 추가
     */
    @EventListener
    public void handleSessionSubscribe(SessionSubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String destination = accessor.getDestination();
        if (destination != null && !destination.endsWith(".count")) {
            String articleId = destination.substring(destination.lastIndexOf(".") + 1);
            String category = accessor.getFirstNativeHeader("category");
            String sessionId = accessor.getSessionId();
            System.out.println("Category: " + category + "ArticleId: "+ articleId);
            chatSessionPort.addSession(articleId, category, sessionId);
        }
    }

    /**
     * 사용자의 웹소켓 구독 해제를 처리하고 세션 정보를 제거한다.
     *
     * @param event 세션 구독 해제 이벤트
     * @author 이해창
     * @since 2025-05-26
     */
    @EventListener
    public void handleSessionUnsubscribe(SessionUnsubscribeEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        chatSessionPort.removeSession(sessionId);
    }

    /**
     * 사용자의 웹소켓 연결 종료를 처리하고 세션 정보를 제거한다.
     *
     * @param event 세션 종료 이벤트
     * @author 이해창
     * @since 2025-05-26
     */
    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = accessor.getSessionId();
        chatSessionPort.removeSession(sessionId);
    }

}
