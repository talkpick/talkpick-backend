package com.likelion.backendplus4.talkpick.backend.chat.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;


import java.time.format.DateTimeFormatter;

/**
 * RabbitMQ 기반 채팅 인프라를 설정하는 Configuration 클래스.
 * TopicExchange, Queue, Binding, 및 RabbitTemplate을 빈으로 등록합니다.
 *
 * @since 2025-05-18
 */
@Configuration
public class RabbitMQConfig {

    // ISO-8601 Timestamp Formatter
    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private final String rabbitUser;
    private final String rabbitPassword;
    private final String rabbitHost;
    private final int rabbitPort;

    private static final String CHAT_EXCHANGE_NAME = "chat.exchange";
    private static final String CHAT_QUEUE_NAME = "chat.queue.default";
    private static final String ROUTING_PATTERN = "chat.article.*";

    public RabbitMQConfig(
            @Value("${spring.rabbitmq.username}") String rabbitUser,
            @Value("${spring.rabbitmq.password}") String rabbitPassword,
            @Value("${spring.rabbitmq.host}") String rabbitHost,
            @Value("${spring.rabbitmq.port}") int rabbitPort
    ) {
        this.rabbitUser = rabbitUser;
        this.rabbitPassword = rabbitPassword;
        this.rabbitHost = rabbitHost;
        this.rabbitPort = rabbitPort;
    }

    /**
     * ConnectionFactory를 생성하고 RabbitMQ 연결 정보를 설정합니다.
     * @return CachingConnectionFactory 인스턴스
     * @since 2025-05-18
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cf = new CachingConnectionFactory(rabbitHost, rabbitPort);
        cf.setUsername(rabbitUser);
        cf.setPassword(rabbitPassword);
        return cf;
    }

    /**
     * RabbitAdmin를 생성하고 브로커에 선언된 Exchange, Queue, Binding을 자동으로 등록합니다.
     *
     * @param connectionFactory 브로커 연결을 위한 ConnectionFactory
     * @return 리소스 선언을 관리하는 RabbitAdmin 인스턴스
     * @author 이해창
     * @since 2025-05-18
     */
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    /**
     * 메시지 라우팅을 담당하는 exchange
     * Binding을 통해 Queue를 연결합니다.
     * <p>durable=true 로 설정하여 브로커 재시작 시에도 큐가 유지되도록 합니다.</p>
     *
     * @return CHAT_EXCHANGE_NAME 라는 이름의 TopicExchange 인스턴스
     * @author 이해창
     * @since 2025-05-18
     */
    @Bean
    public TopicExchange chatExchange() {
        return ExchangeBuilder.topicExchange(CHAT_EXCHANGE_NAME)
                .durable(true)
                .build();
    }

    /**
     * 채팅 메시지 수신용 Durable 큐를 생성합니다.
     *
     * @return CHAT_QUEUE_NAME 라는 이름의 Queue 인스턴스
     * @author 이해창
     * @since 2025-05-18
     */
    @Bean
    public Queue chatQueue() {
        return QueueBuilder.durable(CHAT_QUEUE_NAME).build();
    }

    /**
     * exchange와 queue를 바인딩하며, 바인딩 조건으로 ROUTING_PATTERN을 사용합니다.
     *
     * @param chatQueue    메시지를 수신할 Queue
     * @param chatExchange 메시지를 발행할 TopicExchange
     * @return 구성된 Binding 인스턴스
     * @since 2025-05-18
     */
    @Bean
    public Binding bindingStandard(Queue chatQueue, TopicExchange chatExchange) {
        return BindingBuilder.bind(chatQueue)
                .to(chatExchange)
                .with(ROUTING_PATTERN);
    }


    /**
     * 메시지 발행을 위한 RabbitTemplate
     * 기본 라우팅 키를 ROUTING_PATTERN으로 설정합니다.
     * JSON 직렬화를 위해 MessageConverter를 사용합니다.
     * @param connectionFactory 브로커 연결을 위한 ConnectionFactory
     * @param messageConverter JSON 직렬화를 위한 MessageConverter
     * @return RabbitTemplate
     * @author 이해창
     * @since 2025-05-18
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        rabbitTemplate.setRoutingKey(ROUTING_PATTERN);
        return rabbitTemplate;
    }

    /**
     * JSON으로 직렬화/역직렬화하는 MessageConverter
     * @return Jackson2JsonMessageConverter
     * @author 이해창
     * @since 2025-05-18
     */
    @Bean
    public Jackson2JsonMessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
