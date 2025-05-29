package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.redis;

import java.time.Duration;
import java.util.Map;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageStreamPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

import lombok.RequiredArgsConstructor;

/**
 * 채팅 메시지를 Redis Stream에 저장하는 어댑터입니다.
 *
 * 메시지를 JSON으로 직렬화하여 Redis Stream에 기록하고,
 * 스트림 키에 대해 TTL(Duration: 3일)을 설정합니다.
 *
 * @since 2025-05-22
 */
@Component
@RequiredArgsConstructor
public class RedisStreamAdapter implements ChatMessageStreamPort {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;
	private static final String STREAM_KEY_PREFIX = "chat:stream:";
	private static final Duration STREAM_TTL = Duration.ofDays(3);

	/**
	 * 채팅 메시지를 Redis Stream에 저장합니다.
	 *
	 * @param message 저장할 채팅 메시지 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-22
	 */
	@Override
	public void cacheToStream(ChatMessage message) {
		String key = STREAM_KEY_PREFIX + message.getArticleId();

		String payload = toJson(message);

		MapRecord<String, String, String> record = toRecord(key, payload);

		redisTemplate.opsForStream().add(record);
		redisTemplate.expire(key, STREAM_TTL);
	}

	/**
	 * ChatMessage 객체를 JSON 문자열로 직렬화합니다.
	 *
	 * @param message 직렬화할 채팅 메시지
	 * @return 직렬화된 JSON 문자열
	 * @throws RuntimeException 직렬화 실패 시 예외 발생
	 * @author 박찬병
	 * @since 2025-05-22
	 */
	private String toJson(ChatMessage message) {
		try {
			return objectMapper.writeValueAsString(message);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Redis Stream payload 직렬화 실패", e);
		}
	}


	/**
	 * Redis Stream에 저장할 MapRecord 객체를 생성합니다.
	 *
	 * @param key     Redis Stream 키
	 * @param payload 직렬화된 채팅 메시지
	 * @return Redis에 저장할 MapRecord 객체
	 * @author 박찬병
	 * @since 2025-05-22
	 */
	private MapRecord<String, String, String> toRecord(String key, String payload) {
		return StreamRecords
			.mapBacked(Map.of("payload", payload))
			.withStreamKey(key);
	}


}