package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.batch.executor;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ArticleEntity;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;

import lombok.RequiredArgsConstructor;

/**
 * Redis에 저장된 뉴스 조회수 데이터를 DB로 동기화하는 Quartz Job.
 * 매일 자정에 실행되며, 오래된 데이터는 정리 정책에 따라 관리합니다.
 * - 최근 7일 데이터: 모두 유지
 * - 8~30일 데이터: 조회수 1000 이상만 유지
 * - 30일 이후 데이터: 모두 삭제
 *
 * @modified 2025-05-20
 * @since 2025-05-19
 */
@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class ViewCountSyncJobExecutor implements org.quartz.Job {

    private static final String VIEW_COUNT_KEY_PREFIX = "news:viewCount:";
    private static final String VIEW_COUNT_PATTERN = "news:viewCount:*";
    private static final int DATA_RETENTION_DAYS = 30;
    private static final int RECENT_DATA_DAYS = 7;
    private static final long MIN_VIEW_COUNT_THRESHOLD = 1000L;

    private final RedisTemplate<String, String> redisTemplate;
    private final NewsInfoJpaRepository newsInfoJpaRepository;

    /**
     * Quartz 트리거에 의해 호출되는 실행 메서드.
     * Redis에 저장된 조회수 데이터를 DB로 동기화하고, 정리 정책에 따라 데이터를 관리합니다.
     *
     * @param context Quartz 실행 컨텍스트
     * @throws NewsInfoException 작업 실행 중 오류 발생 시
     */
    @Override
    @Transactional
    public void execute(JobExecutionContext context) {
        executeSync();
    }

    private void executeSync() {
        try {
            syncViewCountToDb();
            cleanupOldData();
        } catch (Exception e) {
            handleSyncException(e);
        }
    }

    private void handleSyncException(Exception e) {
        throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_SYNC_FAILED, e);
    }

    /**
     * Redis에 저장된 모든 조회수 데이터를 DB에 동기화합니다.
     *
     * @throws NewsInfoException 동기화 작업 중 오류 발생 시
     */
    private void syncViewCountToDb() {
        Set<String> keys = getRedisViewCountKeys();

        if (keys.isEmpty()) {
            return;
        }

        keys.forEach(this::processKeySyncSafely);
    }

    /**
     * 키 동기화 처리를 안전하게 수행합니다.
     * 개별 키 처리 실패는 전체 작업에 영향을 주지 않습니다.
     *
     * @param key 처리할 Redis 키
     */
    private void processKeySyncSafely(String key) {
        try {
            processKeySync(key);
        } catch (Exception e) {
        }
    }

    /**
     * 하나의 키에 대한 동기화 처리를 수행합니다.
     *
     * @param key 처리할 Redis 키
     * @throws NewsInfoException 처리 중 오류 발생 시
     */
    private void processKeySync(String key) {
        String newsId = extractNewsIdFromKey(key);

        Optional.ofNullable(getRedisValue(key))
                .map(value -> parseViewCount(value))
                .ifPresent(viewCount -> updateArticleViewCount(newsId, viewCount));
    }

    /**
     * Redis에서 조회수 관련 키를 모두 가져옵니다.
     *
     * @return 조회수 키 집합 (비어있을 수 있음)
     */
    private Set<String> getRedisViewCountKeys() {
        return Optional.ofNullable(redisTemplate.keys(VIEW_COUNT_PATTERN))
                .orElse(Set.of());
    }

    /**
     * Redis에서 키에 해당하는 값을 가져옵니다.
     *
     * @param key Redis 키
     * @return 키에 저장된 값 (없으면 null)
     */
    private String getRedisValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Redis 키에서 뉴스 ID를 추출합니다.
     *
     * @param key Redis 키
     * @return 뉴스 ID
     */
    private String extractNewsIdFromKey(String key) {
        return key.substring(VIEW_COUNT_KEY_PREFIX.length());
    }

    /**
     * 조회수 문자열 값을 숫자로 파싱합니다.
     *
     * @param countValue 파싱할 조회수 문자열
     * @return 파싱된 조회수
     * @throws NewsInfoException 파싱 실패 시
     */
    private Long parseViewCount(String countValue) {
        try {
            return Long.parseLong(countValue);
        } catch (NumberFormatException e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_INVALID_FORMAT, e);
        }
    }

    /**
     * 특정 뉴스 ID의 조회수를 DB에 업데이트합니다.
     *
     * @param newsId    뉴스 ID
     * @param viewCount 조회수
     * @throws NewsInfoException 뉴스 정보를 찾을 수 없거나 업데이트 실패 시
     */
    private void updateArticleViewCount(String newsId, Long viewCount) {
        newsInfoJpaRepository.findByGuid(newsId)
                .stream()
                .findFirst()
                .ifPresentOrElse(
                        article -> updateAndSaveArticle(article, viewCount),
                        () -> {
                            throw new NewsInfoException(NewsInfoErrorCode.NEWS_INFO_NOT_FOUND);
                        }
                );
    }

    /**
     * 기사 엔티티의 조회수를 업데이트하고 저장합니다.
     *
     * @param article   기사 엔티티
     * @param viewCount 새 조회수
     * @throws NewsInfoException 저장 실패 시
     */
    private void updateAndSaveArticle(ArticleEntity article, Long viewCount) {
        try {
            article.setViewCount(viewCount);
            newsInfoJpaRepository.save(article);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * 오래된 조회수 데이터를 정리 정책에 따라 관리합니다.
     *
     * @throws NewsInfoException 데이터 정리 작업 중 오류 발생 시
     */
    private void cleanupOldData() {
        Set<String> keys = getRedisViewCountKeys();

        if (keys.isEmpty()) {
            return;
        }

        keys.forEach(this::processKeyCleanupSafely);
    }

    /**
     * 키 정리 처리를 안전하게 수행합니다.
     * 개별 키 처리 실패는 전체 작업에 영향을 주지 않습니다.
     *
     * @param key 처리할 Redis 키
     */
    private void processKeyCleanupSafely(String key) {
        try {
            processKeyCleanup(key);
        } catch (Exception e) {
        }
    }

    /**
     * 하나의 키에 대한 정리 작업을 처리합니다.
     *
     * @param key 처리할 Redis 키
     * @throws NewsInfoException 처리 중 오류 발생 시
     */
    private void processKeyCleanup(String key) {
        Long ttl = getTtl(key);

        if (isInvalidTtl(ttl)) {
            return;
        }

        Optional.ofNullable(getRedisValue(key))
                .map(this::parseViewCount)
                .ifPresent(viewCount -> processDataRetention(key, ttl, viewCount));
    }

    /**
     * 데이터 보존 정책에 따라 키 처리를 수행합니다.
     *
     * @param key       Redis 키
     * @param ttl       TTL 값
     * @param viewCount 조회수
     */
    private void processDataRetention(String key, Long ttl, Long viewCount) {
        Long remainingDays = DATA_RETENTION_DAYS - ttl;

        DataRetentionCategory category = getDataRetentionCategory(remainingDays, viewCount);

        switch (category) {
            case DELETE_OLD_DATA, DELETE_LOW_VIEWS -> deleteKey(key);
            case KEEP -> {
            }
        }
    }

    /**
     * 데이터 보존 카테고리를 결정합니다.
     *
     * @param remainingDays 남은 일수
     * @param viewCount     조회수
     * @return 데이터 보존 카테고리
     */
    private DataRetentionCategory getDataRetentionCategory(Long remainingDays, Long viewCount) {
        if (remainingDays >= DATA_RETENTION_DAYS) {
            return DataRetentionCategory.DELETE_OLD_DATA;
        }

        if (remainingDays >= RECENT_DATA_DAYS && viewCount < MIN_VIEW_COUNT_THRESHOLD) {
            return DataRetentionCategory.DELETE_LOW_VIEWS;
        }

        return DataRetentionCategory.KEEP;
    }

    /**
     * TTL이 유효하지 않은지 확인합니다.
     *
     * @param ttl TTL 값
     * @return 유효하지 않으면 true
     */
    private boolean isInvalidTtl(Long ttl) {
        return ttl == null || ttl < 0;
    }

    /**
     * Redis 키의 TTL을 조회합니다.
     *
     * @param key 조회할 키
     * @return TTL 값 (일 단위)
     */
    private Long getTtl(String key) {
        return redisTemplate.getExpire(key, TimeUnit.DAYS);
    }

    /**
     * Redis에서 키를 삭제합니다.
     *
     * @param key 삭제할 키
     */
    private void deleteKey(String key) {
        redisTemplate.delete(key);
    }

    /**
     * 데이터 보존 카테고리 열거형
     * DELETE_OLD_DATA,    30일 이상 된 데이터
     * DELETE_LOW_VIEWS,   7일 이상 된 데이터 중 조회수 1000 미만
     * KEEP                유지할 데이터
     */
    private enum DataRetentionCategory {
        DELETE_OLD_DATA,
        DELETE_LOW_VIEWS,
        KEEP
    }
}