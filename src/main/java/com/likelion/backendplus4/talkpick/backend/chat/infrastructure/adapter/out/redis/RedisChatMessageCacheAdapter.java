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

@Component
@RequiredArgsConstructor
public class RedisChatMessageCacheAdapter implements ChatMessageCachePort {

	private final RedisTemplate<String, String> redisTemplate;
	private final ObjectMapper objectMapper;

	@Override
	public void cache(ChatMessage message) {
		try {
			String json = objectMapper.writeValueAsString(message);
			String key = getKey(message.getArticleId());

			redisTemplate.opsForList().leftPush(key, json);
			redisTemplate.expire(key, Duration.ofDays(3));
		} catch (JsonProcessingException e) {
			throw new RuntimeException("Redis 캐싱 실패", e);
		}
	}

	@Override
	public List<ChatMessage> getRecentMessages(String articleId) {
		List<String> cached = redisTemplate.opsForList().range(getKey(articleId), 0, 99);
		return Objects.requireNonNull(cached).stream()
			.map(json -> {
				try {
					return objectMapper.readValue(json, ChatMessage.class);
				} catch (JsonProcessingException e) {
					return null;
				}
			})
			.filter(Objects::nonNull)
			.toList();
	}


	private String getKey(String articleId) {
		return "chatroom:" + articleId + ":messages";
	}
}
