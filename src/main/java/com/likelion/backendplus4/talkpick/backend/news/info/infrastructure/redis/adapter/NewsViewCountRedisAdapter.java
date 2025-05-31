package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.adapter;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsViewCountPort;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util.RedisKeyGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 뉴스 조회수 관리를 담당하는 Redis 어댑터입니다.
 *
 * 조회수 증가, 조회, 조회 이력 관리 등의 기능을 제공합니다.
 *
 * @author 양병학
 * @since 2025-05-27 최초 작성
 */
@Component
@RequiredArgsConstructor
public class NewsViewCountRedisAdapter implements NewsViewCountPort {

    private static final int RECENT_NEWS_DAYS = 3;
    private static final int VIEW_HISTORY_EXPIRE_MINUTES = 10;
    private static final int VIEW_COUNT_EXPIRE_DAYS = 30;

    private final RedisTemplate<String, String> redisTemplate;
    private final NewsInfoJpaRepository newsInfoJpaRepository;
    private final RedisKeyGenerator keyGenerator;
    private final PopularNewsRedisAdapter popularNewsAdapter;

    /**
     * 뉴스 조회수를 증가시킵니다.
     *
     * 1. 현재 조회수 확인 (Redis → DB 순서)
     * 2. 조회수 증가 처리
     * 3. 조회 이력 저장
     * 4. 최근 뉴스인 경우 랭킹 업데이트
     *
     * @param newsId      조회수를 증가시킬 뉴스의 ID
     * @param ipAddress   사용자 IP 주소
     * @param category    뉴스 카테고리
     * @param publishDate 뉴스 발행일
     * @return 증가된 후의 조회수 값
     * @throws NewsInfoException 조회수 증가 처리 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public Long increaseViewCount(String newsId, String ipAddress, String category, LocalDateTime publishDate) {
        Long newViewCount = processViewCountIncrease(newsId);
        saveUserViewHistory(newsId, ipAddress);

        if (isRecentNews(publishDate)) {
            updateRankingIfNeeded(category, newsId, newViewCount, publishDate);
        }

        return newViewCount;
    }

    /**
     * 사용자의 뉴스 조회 이력을 저장합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 저장 성공 여부
     * @throws NewsInfoException Redis 저장 실패 시
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public boolean saveViewHistory(String newsId, String ipAddress) {
        return saveUserViewHistory(newsId, ipAddress);
    }

    /**
     * 사용자가 특정 뉴스를 이미 조회했는지 확인합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 조회 이력 존재 여부
     * @throws NewsInfoException Redis 조회 실패 시
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public boolean hasViewHistory(String newsId, String ipAddress) {
        return checkUserViewHistory(newsId, ipAddress);
    }

    /**
     * 특정 뉴스의 현재 조회수를 조회합니다.
     *
     * @param newsId 조회할 뉴스의 ID
     * @return 현재 조회수
     * @throws NewsInfoException 조회 실패 시
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public Long getCurrentViewCount(String newsId) {
        return retrieveCurrentViewCount(newsId);
    }

    /**
     * 조회수 증가 처리를 수행합니다.
     *
     * @param newsId 뉴스 ID
     * @return 증가된 조회수
     * @throws NewsInfoException 조회수 증가 처리 중 오류가 발생한 경우
     */
    private Long processViewCountIncrease(String newsId) {
        String key = keyGenerator.createViewCountKey(newsId);

        if (isViewCountCached(key)) {
            return incrementCachedViewCount(key);
        } else {
            return initializeAndIncrementViewCount(key, newsId);
        }
    }

    /**
     * Redis에 조회수가 캐시되어 있는지 확인합니다.
     *
     * @param key Redis 키
     * @return 캐시 존재 여부
     */
    private boolean isViewCountCached(String key) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_REDIS_RETRIEVE_FAILED, e);
        }
    }

    /**
     * 캐시된 조회수를 증가시킵니다.
     *
     * @param key Redis 키
     * @return 증가된 조회수
     * @throws NewsInfoException Redis 증가 작업 실패 시
     */
    private Long incrementCachedViewCount(String key) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_REDIS_SAVE_FAILED, e);
        }
    }

    /**
     * 조회수를 초기화하고 증가시킵니다.
     *
     * @param key    Redis 키
     * @param newsId 뉴스 ID
     * @return 증가된 조회수
     * @throws NewsInfoException 초기화 또는 증가 처리 중 오류가 발생한 경우
     */
    private Long initializeAndIncrementViewCount(String key, String newsId) {
        Long dbViewCount = getViewCountFromDatabase(newsId);
        Long newViewCount = dbViewCount + 1;
        saveViewCountToRedis(key, newViewCount);
        return newViewCount;
    }

    /**
     * 데이터베이스에서 조회수를 조회합니다.
     *
     * @param newsId 뉴스 ID
     * @return 조회수 (없을 경우 0)
     * @throws NewsInfoException DB 조회 실패 시
     */
    private Long getViewCountFromDatabase(String newsId) {
        try {
            return newsInfoJpaRepository.findByGuid(newsId)
                    .stream()
                    .findFirst()
                    .map(article -> Objects.requireNonNullElse(article.getViewCount(), 0L))
                    .orElse(0L);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_DB_QUERY_FAILED, e);
        }
    }

    /**
     * Redis에 조회수를 저장합니다.
     *
     * @param key       Redis 키
     * @param viewCount 저장할 조회수
     * @throws NewsInfoException Redis 저장 실패 시
     */
    private void saveViewCountToRedis(String key, Long viewCount) {
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(viewCount));
            redisTemplate.expire(key, VIEW_COUNT_EXPIRE_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_REDIS_SAVE_FAILED, e);
        }
    }

    /**
     * 사용자 조회 이력을 저장합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 저장 성공 여부
     * @throws NewsInfoException 이력 저장 실패 시
     */
    private boolean saveUserViewHistory(String newsId, String ipAddress) {
        try {
            String key = keyGenerator.createViewHistoryKey(newsId, ipAddress);
            redisTemplate.opsForValue().set(key, "1");
            redisTemplate.expire(key, VIEW_HISTORY_EXPIRE_MINUTES, TimeUnit.MINUTES);  // ← 변경
            return true;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_HISTORY_SAVE_FAILED, e);
        }
    }

    /**
     * 사용자 조회 이력을 확인합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 조회 이력 존재 여부
     * @throws NewsInfoException 이력 조회 실패 시
     */
    private boolean checkUserViewHistory(String newsId, String ipAddress) {
        try {
            String key = keyGenerator.createViewHistoryKey(newsId, ipAddress);
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_HISTORY_RETRIEVE_FAILED, e);
        }
    }

    /**
     * 현재 조회수를 조회합니다.
     *
     * @param newsId 뉴스 ID
     * @return 현재 조회수
     * @throws NewsInfoException 조회 실패 시
     */
    private Long retrieveCurrentViewCount(String newsId) {
        Optional<Long> redisViewCount = getViewCountFromRedis(newsId);
        return redisViewCount.orElseGet(() -> getViewCountFromDatabase(newsId));
    }

    /**
     * Redis에서 조회수를 조회합니다.
     *
     * @param newsId 뉴스 ID
     * @return 조회수 Optional
     * @throws NewsInfoException Redis 조회 실패 시
     */
    private Optional<Long> getViewCountFromRedis(String newsId) {
        try {
            String key = keyGenerator.createViewCountKey(newsId);
            String countValue = redisTemplate.opsForValue().get(key);
            return Optional.ofNullable(countValue).map(this::parseViewCount);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_REDIS_RETRIEVE_FAILED, e);
        }
    }

    /**
     * 조회수 문자열을 Long으로 파싱합니다.
     *
     * @param countValue 파싱할 문자열
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
     * 최근 뉴스인지 확인합니다.
     *
     * @param publishDate 발행일
     * @return 최근 뉴스 여부
     */
    private boolean isRecentNews(LocalDateTime publishDate) {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(RECENT_NEWS_DAYS);
        return publishDate.isAfter(threeDaysAgo);
    }

    /**
     * 필요시 랭킹을 업데이트합니다.
     *
     * @param category  카테고리
     * @param newsId    뉴스 ID
     * @param viewCount 조회수
     * @throws NewsInfoException 랭킹 업데이트 실패 시
     */
    private void updateRankingIfNeeded(String category, String newsId, Long viewCount, LocalDateTime publishDate) {
        try {
            popularNewsAdapter.updateRankingScore(category, newsId, viewCount, publishDate);
            popularNewsAdapter.updateRankingScore("전체", newsId, viewCount, publishDate);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.RANKING_SCORE_UPDATE_FAILED, e);
        }
    }
}