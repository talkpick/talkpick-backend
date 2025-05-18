package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.batch.executor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis에 저장된 뉴스 조회수 데이터를 DB로 동기화하는 Quartz Job.
 * 매일 자정에 실행되며, 오래된 데이터는 정리 정책에 따라 관리합니다.
 * - 최근 7일 데이터: 모두 유지
 * - 8~30일 데이터: 조회수 1000 이상만 유지
 * - 30일 이후 데이터: 모두 삭제
 *
 * @since 2025-05-19
 */
@Slf4j
@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class ViewCountSyncJobExecutor implements org.quartz.Job {

	private static final String VIEW_COUNT_KEY_PREFIX = "news:viewCount:";
	private static final String VIEW_COUNT_PATTERN = "news:viewCount:*";

	private final RedisTemplate<String, String> redisTemplate;
	private final NewsInfoJpaRepository newsInfoJpaRepository;

	/**
	 * Quartz 트리거에 의해 호출되는 실행 메서드.
	 * Redis에 저장된 조회수 데이터를 DB로 동기화하고, 정리 정책에 따라 데이터를 관리합니다.
	 *
	 * @param context Quartz 실행 컨텍스트
	 * @throws JobExecutionException 작업 실행 중 오류 발생 시
	 * @since 2025-05-19
	 */
	@Override
	@Transactional
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			log.info("Starting view count synchronization job at {}", LocalDateTime.now());
			syncViewCountToDb();
			cleanupOldData();
			log.info("View count synchronization job completed successfully");
		} catch (Exception e) {
			log.error("Error during view count synchronization job", e);
			throw new JobExecutionException("Failed to synchronize view count data", e);
		}
	}

	/**
	 * Redis에 저장된 모든 조회수 데이터를 DB에 동기화합니다.
	 *
	 * @since 2025-05-19
	 */
	private void syncViewCountToDb() {
		log.info("Starting to sync view count data from Redis to DB");

		// 1. Redis에서 모든 조회수 키 가져오기
		Set<String> keys = redisTemplate.keys(VIEW_COUNT_PATTERN);
		if (keys == null || keys.isEmpty()) {
			log.info("No view count data found in Redis");
			return;
		}

		// 2. 모든 키에 대해 처리
		int updatedCount = 0;
		int errorCount = 0;

		for (String key : keys) {
			String guidValue = key.substring(VIEW_COUNT_KEY_PREFIX.length());
			String countValue = redisTemplate.opsForValue().get(key);

			if (countValue == null) {
				continue;
			}

			try {
				Long viewCount = Long.parseLong(countValue);

				// DB 업데이트
				boolean updated = updateArticleViewCount(guidValue, viewCount);
				if (updated) {
					updatedCount++;
				}
			} catch (NumberFormatException e) {
				log.error("Invalid view count value for key {}: {}", key, countValue, e);
				errorCount++;
			} catch (Exception e) {
				log.error("Error updating view count for key {}", key, e);
				errorCount++;
			}
		}

		log.info("View count sync completed. Updated: {}, Errors: {}", updatedCount, errorCount);
	}

	/**
	 * 특정 뉴스 ID의 조회수를 DB에 업데이트합니다.
	 *
	 * @param newsId 뉴스 ID
	 * @param viewCount 조회수
	 * @return 업데이트 성공 여부
	 * @since 2025-05-19
	 */
	private boolean updateArticleViewCount(String newsId, Long viewCount) {
		return newsInfoJpaRepository.findByGuid(newsId)
			.stream()
			.findFirst()
			.map(article -> {
				article.setViewCount(viewCount);
				newsInfoJpaRepository.save(article);
				log.debug("Updated view count for news ID {}: {}", newsId, viewCount);
				return true;
			})
			.orElseGet(() -> {
				log.warn("News article with ID {} not found in database", newsId);
				return false;
			});
	}

	/**
	 * 오래된 조회수 데이터를 정리 정책에 따라 관리합니다.
	 * - 최근 7일 데이터: 모두 유지
	 * - 8~30일 데이터: 조회수 1000 이상만 유지
	 * - 30일 이후 데이터: 모두 삭제
	 *
	 * @since 2025-05-19
	 */
	private void cleanupOldData() {
		log.info("Starting cleanup of old view count data");

		// 1. Redis에서 모든 조회수 키 가져오기
		Set<String> keys = redisTemplate.keys(VIEW_COUNT_PATTERN);
		if (keys == null || keys.isEmpty()) {
			return;
		}

		// 2. 키별로 TTL 확인 및 정리
		int deleted30DaysPlus = 0;
		int deletedLowViews = 0;

		for (String key : keys) {
			Long ttl = redisTemplate.getExpire(key, TimeUnit.DAYS);
			if (ttl == null || ttl < 0) {
				continue; // TTL이 설정되지 않았거나 영구적인 키는 무시
			}

			String countValue = redisTemplate.opsForValue().get(key);
			if (countValue == null) {
				continue;
			}

			try {
				Long viewCount = Long.parseLong(countValue);
				Long remainingDays = 30 - ttl;

				// 30일 이상 된 데이터 삭제
				if (remainingDays >= 30) {
					redisTemplate.delete(key);
					deleted30DaysPlus++;
					log.debug("Deleted view count data older than 30 days: {}", key);
				}
				// 8~30일 데이터 중 조회수 1000 미만 삭제
				else if (remainingDays >= 8) {
					if (viewCount < 1000) {
						redisTemplate.delete(key);
						deletedLowViews++;
						log.debug("Deleted view count data with count < 1000: {}", key);
					}
				}
				// 7일 이내 데이터는 유지
			} catch (NumberFormatException e) {
				log.error("Invalid view count value for key {}: {}", key, countValue, e);
			}
		}

		log.info("Cleanup completed. Deleted old data: {}, Deleted low view data: {}",
			deleted30DaysPlus, deletedLowViews);
	}
}