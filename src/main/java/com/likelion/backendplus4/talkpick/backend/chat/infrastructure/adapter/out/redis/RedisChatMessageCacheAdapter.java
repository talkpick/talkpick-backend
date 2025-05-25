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
	private static final String MESSAGE_KEY_SUFFIX = ":messages";
	private static final String HAS_NEXT_KEY_SUFFIX = ":hasNext";
	private static final Duration CACHE_TTL = Duration.ofDays(3);
	private static final int DEFAULT_MAX_CACHE_SIZE = 100;

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
	public void cache(ChatMessage message, int maxCacheSize) {
		String key = buildKey(message.getArticleId(), MESSAGE_KEY_SUFFIX);
		String json = toJson(message);

		Long currentSize = redisTemplate.opsForList().leftPush(key, json);

		redisTemplate.opsForList().trim(key, 0, maxCacheSize - 1);
		redisTemplate.expire(key, CACHE_TTL);

		String flagKey = buildKey(message.getArticleId(), HAS_NEXT_KEY_SUFFIX);
		boolean existingHasNext = Boolean.parseBoolean(redisTemplate.opsForValue().get(flagKey));

		if (!existingHasNext && currentSize != null && currentSize > maxCacheSize) {
			updateHasNextFlag(message.getArticleId(), true);
		}
	}

	@Override
	public void cacheMessages(String articleId, List<ChatMessage> recentMessages, boolean hasNext) {
		if (recentMessages == null || recentMessages.isEmpty()) {
			return;
		}

		String key = buildKey(articleId, MESSAGE_KEY_SUFFIX);
		Long size = redisTemplate.opsForList().size(key);

		List<String> jsonList = recentMessages.stream()
			.map(this::toJson)
			.toList();

		if (size == null || size == 0L) {
			pushInitialMessages(key, jsonList);
		} else {
			appendOldMessages(key, jsonList);
		}

		updateHasNextFlag(articleId, hasNext);
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
	public List<ChatMessage> getRecentMessages(String articleId, int maxCacheSize) {
		String key = buildKey(articleId, MESSAGE_KEY_SUFFIX);
		List<String> rawMessages = redisTemplate.opsForList().range(key, 0, maxCacheSize - 1);

		if (rawMessages == null) {
			return List.of();
		}

		return rawMessages.stream()
			.map(this::fromJson)
			.filter(Objects::nonNull)
			.toList();
	}

	@Override
	public boolean getHasNextFlag(String articleId) {
		String flagKey = buildKey(articleId, HAS_NEXT_KEY_SUFFIX);
		String val = redisTemplate.opsForValue().get(flagKey);
		return Boolean.parseBoolean(val == null ? "false" : val);
	}

	/**
	 * Redis 캐시 키를 생성한다.
	 *
	 * @param articleId 채팅방 식별자
	 * @param suffix    키 접미사 (메시지 목록 또는 hasNext 플래그)
	 * @return Redis 캐시 키
	 * @since 2025-05-22
	 */
	private String buildKey(String articleId, String suffix) {
		return LIST_KEY_PREFIX + articleId + suffix;
	}

	/**
	 * hasNext 플래그를 갱신하고 TTL을 설정한다.
	 *
	 * @param articleId 채팅방 식별자
	 * @param hasNext   다음 페이지 존재 여부
	 */
	private void updateHasNextFlag(String articleId, boolean hasNext) {
		String flagKey = buildKey(articleId, HAS_NEXT_KEY_SUFFIX);
		redisTemplate.opsForValue().set(flagKey, Boolean.toString(hasNext), CACHE_TTL);
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

	/**
	 * 캐시가 비어 있을 때 최초로 메시지 100개를 채운다.
	 * 최신 메시지가 인덱스 0이 되도록 역순으로 LPUSH 후 트림한다.
	 */
	private void pushInitialMessages(String key, List<String> jsons) {
		List<String> reversed = jsons.reversed();
		redisTemplate.opsForList().leftPushAll(key, reversed.toArray(new String[0]));
		redisTemplate.opsForList().trim(key, 0, DEFAULT_MAX_CACHE_SIZE - 1);
		redisTemplate.expire(key, CACHE_TTL);
	}

	/**
	 * 이미 새 메시지가 존재할 때 과거 메시지를 꼬리에 붙인다.
	 */
	private void appendOldMessages(String key, List<String> jsons) {
		redisTemplate.opsForList().rightPushAll(key, jsons.toArray(new String[0]));
		redisTemplate.opsForList().trim(key, 0, DEFAULT_MAX_CACHE_SIZE - 1);
	}
}
