package com.likelion.backendplus4.talkpick.backend.common.configuration.redis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 설정을 담당하는 Configuration 클래스입니다.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    /**
     * RedisConnectionFactory를 구성합니다.
     * application.yml에 설정된 host와 port 정보를 사용하여 LettuceConnectionFactory를 생성합니다.
     *
     * @return LettuceConnectionFactory 객체
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    /**
     * RedisTemplate<String, String>를 구성합니다. 키와 값, 해시 키와 해시 값을 모두 StringRedisSerializer로 직렬화하도록 설정합니다.
     *
     * @param redisConnectionFactory RedisConnectionFactory 객체
     * @return RedisTemplate<String, String> 객체
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Bean
    public RedisTemplate<String, String> stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {

        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}
