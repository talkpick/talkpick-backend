package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import org.springframework.web.socket.WebSocketSession;

public interface ChatSessionPort {
    void addSession(String articleId, String sessionId);
    void removeSession(String sessionId);
    int getSessionCount(String articleId);
}
