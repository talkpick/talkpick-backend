package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.interceptor;

import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();
        String type = accessor.getFirstNativeHeader("type");
        if (type != null && type.equals("PUBLIC")) {
            return message;
        }
        if (StompCommand.CONNECT == command) {
            if (accessor.getFirstNativeHeader("Authorization") == null) {
                throw new AuthException(AuthErrorCode.AUTHENTICATION_REQUIRED);
            }
        }

        return message;
    }
}
