package com.likelion.backendplus4.talkpick.backend.common.configuration.websocket;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocket 및 STOMP 기반 메시징을 설정하는 Configuration 클래스.
 * RabbitMQ를 브로커로 사용하기 위해 STOMP 브로커 릴레이를 활성화합니다.
 *
 * @since 2025-05-18
 * @modified 2025-05-20
 * 2025.05.20 - 폴더 위치 이동
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final String relayHost;
    private final int relayPort;
    private final String username;
    private final String password;

    /**
     * 생성자 주입을 통해 프로퍼티 값을 받습니다.
     *
     * @param relayHost 브로커 호스트 (spring.rabbitmq.host)
     * @param relayPort 브로커 STOMP 포트 (spring.rabbitmq.stomp-port)
     * @param username  브로커 아이디 (spring.rabbitmq.username)
     * @param password  브로커 비밀번호 (spring.rabbitmq.password)
     * @author 이해창
     * @since 2025-05-18
     */
    public WebSocketConfig(
            @Value("${spring.rabbitmq.host}") String relayHost,
            @Value("${spring.rabbitmq.stomp-port}") int relayPort,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password
    ) {
        this.relayHost = relayHost;
        this.relayPort = relayPort;
        this.username = username;
        this.password = password;
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
                .withSockJS();
    }

    /**
     * 메시지 브로커 설정
     * appDestinationPrefixes: 클라이언트 -> 서버로 메시지를 보낼 때 사용할 prefix
     * enableStompBrokerRelay: RabbitMQ STOMP 브로커 릴레이 설정
     *
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


}
