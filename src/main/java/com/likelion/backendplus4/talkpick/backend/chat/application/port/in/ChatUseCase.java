package com.likelion.backendplus4.talkpick.backend.chat.application.port.in;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

public interface ChatUseCase {
    void sendMessage(ChatMessage message);
    void receiveMessage(ChatMessage message);
//    void subscribe(SessionSubscribeEvent event);
}
