package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.redis;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageCachePort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

import lombok.RequiredArgsConstructor;

/**
 * Redis 기반으로 채팅 메시지를 캐싱하는 어댑터.
 * - 최근 100개 메시지만 유지하며, TTL은 3일로 설정된다.
 *
 * @since 2025-05-22
 */
@Component
@RequiredArgsConstructor
public class RedisChatMessageCacheAdapter implements ChatMessageCachePort {

	private static final String LIST_KEY_PREFIX = "chat:list:";
	private static final Duration CACHE_TTL = Duration.ofDays(3);
	private static final int MAX_CACHE_SIZE = 100;

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	/**
	 * 채팅 메시지를 Redis List에 캐싱한다.
	 *
	 * @param message 캐싱할 채팅 메시지
	 * @author 박찬병
	 * @since 2025-05-22
	 */
	@Override
	public void cache(ChatMessage message) {
		String key = buildKey(message.getArticleId());
		String json = toJson(message);

		redisTemplate.opsForList().leftPush(key, json);
		redisTemplate.opsForList().trim(key, 0, MAX_CACHE_SIZE - 1);
		redisTemplate.expire(key, CACHE_TTL);
	}

	/**
	 * 최근 캐싱된 채팅 메시지를 조회한다.
	 *
	 * @param articleId 채팅방 식별자
	 * @return 최근 채팅 메시지 목록
	 * @author 박찬병
	 * @since 2025-05-22
	 */
	@Override
	public List<ChatMessage> getRecentMessages(String articleId) {
		String key = buildKey(articleId);
		List<String> rawMessages = redisTemplate.opsForList().range(key, 0, MAX_CACHE_SIZE - 1);

		if (rawMessages == null) {
			return List.of();
		}

		return rawMessages.stream()
			.map(this::fromJson)
			.filter(Objects::nonNull)
			.toList();
	}

	/**
	 * Redis 캐시 키를 생성한다.
	 *
	 * @param articleId 채팅방 식별자
	 * @return Redis 캐시 키
	 * @since 2025-05-22
	 */
	private String buildKey(String articleId) {
		return LIST_KEY_PREFIX + articleId + ":messages";
	}

	/**
	 * 채팅 메시지를 JSON 문자열로 변환한다.
	 *
	 * @param message 변환할 채팅 메시지
	 * @return JSON 문자열
	 * @throws RuntimeException JSON 변환 실패 시 발생
	 * @since 2025-05-22
	 */
	private String toJson(ChatMessage message) {
		try {
			return objectMapper.writeValueAsString(message);
		} catch (JsonProcessingException e) {
			throw new RuntimeException("JSON 변환 실패: " + message, e);
		}
	}

	/**
	 * JSON 문자열을 채팅 메시지 객체로 변환한다.
	 *
	 * @param json 변환할 JSON 문자열
	 * @return 채팅 메시지 객체 (변환 실패 시 null)
	 * @since 2025-05-22
	 */
	private ChatMessage fromJson(String json) {
		try {
			return objectMapper.readValue(json, ChatMessage.class);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
}
