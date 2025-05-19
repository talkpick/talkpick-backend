package com.likelion.backendplus4.talkpick.backend.chat.config;


import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.SecurityPort;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.JwtAuthentication;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

/**
 * WebSocket 및 STOMP 기반 메시징을 설정하는 Configuration 클래스.
 * RabbitMQ를 브로커로 사용하기 위해 STOMP 브로커 릴레이를 활성화합니다.
 *
 * @since 2025-05-18
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final String relayHost;
    private final int relayPort;
    private final String username;
    private final String password;
    private final JwtAuthentication jwtAuthentication;

    /**
     * 생성자 주입을 통해 프로퍼티 값을 받습니다.
     * @param relayHost 브로커 호스트 (spring.rabbitmq.host)
     * @param relayPort 브로커 STOMP 포트 (spring.rabbitmq.stomp-port)
     * @param username 브로커 아이디 (spring.rabbitmq.username)
     * @param password 브로커 비밀번호 (spring.rabbitmq.password)
     * @author 이해창
     * @since 2025-05-18
     */
    public WebSocketConfig(
            @Value("${spring.rabbitmq.host}") String relayHost,
            @Value("${spring.rabbitmq.stomp-port}") int relayPort,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            JwtAuthentication jwtAuthentication) {
        this.relayHost = relayHost;
        this.relayPort = relayPort;
        this.username = username;
        this.password = password;
        this.jwtAuthentication = jwtAuthentication;
    }
    /**
     * 클라이언트가 연결할 STOMP 엔드포인트 등록
     *
     * @param registry StompEndpointRegistry
     * @author 이해창
     * @since 2025-05-18
     */
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws-chat")
                .addInterceptors(new HttpSessionHandshakeInterceptor())
//                .setAllowedOriginPatterns("*") // TODO: nginx 설정 시 제거
                .withSockJS();
    }

    /**
     * 메시지 브로커 설정
     * appDestinationPrefixes: 클라이언트 -> 서버로 메시지를 보낼 때 사용할 prefix
     * enableStompBrokerRelay: RabbitMQ STOMP 브로커 릴레이 설정

     * @param config MessageBrokerRegistry
     * @author 이해창
     * @since 2025-05-18
     */
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 destination prefix
        config.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(relayHost)
                .setRelayPort(relayPort)
                .setClientLogin(username)
                .setClientPasscode(password)
                .setSystemLogin(username)
                .setSystemPasscode(password);

        // 클라이언트 -> 서버 메시지 prefix
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor =
                        MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        System.out.println("Received authentication header: " + authHeader);
                        String token = authHeader.substring(7);
                        Authentication auth = jwtAuthentication.validateAndGetAuthentication(token);
                        accessor.setUser(auth);
                        System.out.println("User authenticated: " + auth.getName());
                        System.out.println(auth.getPrincipal());
                        System.out.println(auth.getAuthorities());
                    }
                }
                return message;
            }
        });
    }
}
