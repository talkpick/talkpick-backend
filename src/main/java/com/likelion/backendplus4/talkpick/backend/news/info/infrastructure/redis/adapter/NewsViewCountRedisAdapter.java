package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.adapter;

import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.application.port.out.NewsViewCountPort;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class NewsViewCountRedisAdapter implements NewsViewCountPort {

	private static final String VIEW_COUNT_KEY_PREFIX = "news:viewCount:";
	private static final String VIEW_HISTORY_KEY_PREFIX = "news:viewHistory:";
	private static final int VIEW_HISTORY_EXPIRE_DAYS = 1;
	private static final int VIEW_COUNT_EXPIRE_DAYS = 30;

	private final RedisTemplate<String, String> redisTemplate;
	private final NewsInfoJpaRepository newsInfoJpaRepository;

	/**
	 * 뉴스 조회수를 증가시키는 메서드입니다.
	 *
	 * 1. Redis에 조회수 키가 존재하는지 확인
	 * 2. 키가 없으면 DB에서 초기값을 가져와 Redis에 저장 (Cold data)
	 * 3. 키가 있으면 Redis에서 직접 증가시킴 (Hot data)
	 *
	 * @param newsId 조회수를 증가시킬 뉴스의 ID
	 * @return 증가된 후의 조회수 값
	 * @since 2025-05-19 최초 작성
	 * @author 양병학
	 */
	@Override
	public Long increaseViewCount(String newsId) {
		String key = VIEW_COUNT_KEY_PREFIX + newsId;
		String currentCount = redisTemplate.opsForValue().get(key);

		return currentCount == null
			? handleColdData(key, newsId)
			: handleHotData(key, newsId);
	}

	/**
	 * Redis에 뉴스 조회수 정보가 없을 때 초기화하는 메서드입니다.
	 *
	 * 1. DB에서 현재 조회수를 조회
	 * 2. 조회수를 1 증가시켜 Redis에 저장
	 * 3. 만료 기간 설정
	 *
	 * @param key Redis에 저장할 키 값
	 * @param newsId 뉴스 ID
	 * @return 증가된 후의 조회수 값
	 * @since 2025-05-19 최초 작성
	 * @author 양병학
	 */
	private Long handleColdData(String key, String newsId) {
		Long dbCount = getViewCountFromDb(newsId);
		Long newCount = dbCount + 1;

		redisTemplate.opsForValue().set(key, String.valueOf(newCount));
		redisTemplate.expire(key, VIEW_COUNT_EXPIRE_DAYS, TimeUnit.DAYS);

		return newCount;
	}

	/**
	 * Redis에서 조회수 직접 증가
	 *
	 * @param key Redis에 저장된 키 값
	 * @param newsId 뉴스 ID
	 * @return 증가된 후의 조회수 값
	 * @since 2025-05-19 최초 작성
	 * @author 양병학
	 *
	 */
	private Long handleHotData(String key, String newsId) {
		Long newCount = redisTemplate.opsForValue().increment(key);

		return newCount;
	}

	@Override
	public boolean saveViewHistory(String newsId, String ipAddress) {
		String key = VIEW_HISTORY_KEY_PREFIX + newsId + ":" + ipAddress;
		redisTemplate.opsForValue().set(key, "1");
		redisTemplate.expire(key, VIEW_HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);
		return true;
	}

	@Override
	public boolean hasViewHistory(String newsId, String ipAddress) {
		String key = VIEW_HISTORY_KEY_PREFIX + newsId + ":" + ipAddress;
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}

	private Long getViewCountFromDb(String newsId) {
		return newsInfoJpaRepository.findByGuid(newsId)
			.stream()
			.findFirst()
			.map(article -> {
				return Objects.requireNonNullElse(article.getViewCount(), 0L);
			})
			.orElse(0L);
	}

	private void updateViewCountInDb(String newsId, Long newCount) {
		newsInfoJpaRepository.findByGuid(newsId)
			.stream()
			.findFirst()
			.ifPresent(article -> {
				article.setViewCount(newCount);
				newsInfoJpaRepository.save(article);
			});
	}
}