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

/**
 * 웹소켓 채널 메시지 전송 시 헤더를 검사하여 인증 여부를 검증하는 인터셉터
 * <p>
 * PUBLIC 타입 메시지는 인증 없이 허용되며, CONNECT 명령에 한해 Authorization 헤더가 없을 경우 인증 예외를 발생시킨다.
 * </p>
 * @since 2025-05-26
 */
@Component
@RequiredArgsConstructor
public class ChatInterceptor implements ChannelInterceptor {

    /**
     * 웹소켓 메시지 전송 전 헤더 정보를 확인하고 인증 검사를 수행한다.
     * <p>
     * PUBLIC 타입 메시지는 인증을 생략하고, 그 외에는 CONNECT 시 Authorization 헤더 존재를 확인한다. <br/>
     * 인증이 필요없는 메시지는 프론트엔드에서 PUBLIC 속성을 담아서 보내게 설정되어 있다.
     * </p>
     * @param message 전달되는 메시지 객체
     * @param channel 메시지가 전송될 채널
     * @return 인증 검사를 통과한 메시지 객체
     * @throws AuthException Authorization 헤더가 없는 CONNECT 요청일 경우 발생
     * @author 이해창
     * @since 2025-05-26
     */
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
