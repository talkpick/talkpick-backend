package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;


public interface ChatSessionPort {
    void addSession(String articleId, String sessionId);
    void removeSession(String sessionId);
    int getSessionCount(String articleId);
}
