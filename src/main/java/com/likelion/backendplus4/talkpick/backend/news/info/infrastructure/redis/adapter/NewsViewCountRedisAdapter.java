package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.adapter;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.application.port.out.NewsViewCountPort;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Component
public class NewsViewCountRedisAdapter implements NewsViewCountPort {

    private static final String VIEW_COUNT_KEY_PREFIX = "news:viewCount:";
    private static final String VIEW_HISTORY_KEY_PREFIX = "news:viewHistory:";
    private static final int VIEW_HISTORY_EXPIRE_MINUTES = 10;
    private static final int VIEW_COUNT_EXPIRE_DAYS = 30;

    private final RedisTemplate<String, String> redisTemplate;
    private final NewsInfoJpaRepository newsInfoJpaRepository;

    public NewsViewCountRedisAdapter(RedisTemplate<String, String> redisTemplate,
                                     NewsInfoJpaRepository newsInfoJpaRepository) {
        this.redisTemplate = redisTemplate;
        this.newsInfoJpaRepository = newsInfoJpaRepository;
    }

    /**
     * 뉴스 조회수를 증가시키는 메서드입니다.
     *
     * @param newsId    조회수를 증가시킬 뉴스의 ID
     * @param ipAddress 사용자의 IP 주소
     * @return 증가된 후의 조회수 값
     */
    @Override
    public Long increaseViewCount(String newsId, String ipAddress) {
        Long newCount = processViewCountIncrement(newsId);
        recordViewHistory(newsId, ipAddress);
        return newCount;
    }

    /**
     * 사용자의 뉴스 조회 이력을 저장합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 저장 성공 여부
     */
    @Override
    public boolean saveViewHistory(String newsId, String ipAddress) {
        return recordViewHistory(newsId, ipAddress);
    }

    /**
     * 사용자가 특정 뉴스를 이미 조회했는지 확인합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 조회 이력 존재 여부
     */
    @Override
    public boolean hasViewHistory(String newsId, String ipAddress) {
        return checkViewHistory(newsId, ipAddress);
    }

    /**
     * 특정 뉴스의 현재 조회수를 조회합니다.
     *
     * @param newsId 조회할 뉴스의 ID
     * @return 현재 조회수
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
     * @throws NewsInfoException 처리 실패 시
     */
    private Long processViewCountIncrement(String newsId) {
        try {
            String key = createViewCountKey(newsId);
            String currentCount = redisTemplate.opsForValue().get(key);

            return currentCount == null
                    ? handleColdData(key, newsId)
                    : handleHotData(key, newsId);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * Redis에 뉴스 조회수 정보가 없을 때 초기화합니다.
     */
    private Long handleColdData(String key, String newsId) {
        try {
            Long dbCount = getViewCountFromDb(newsId);
            Long newCount = dbCount + 1;
            saveViewCountToRedis(key, newCount);
            return newCount;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * Redis에서 조회수를 직접 증가시킵니다.
     */
    private Long handleHotData(String key, String newsId) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    // ===== 조회 이력 관련 =====

    /**
     * 조회 이력을 기록합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 기록 성공 여부
     * @throws NewsInfoException 처리 실패 시
     */
    private boolean recordViewHistory(String newsId, String ipAddress) {
        try {
            String key = createViewHistoryKey(newsId, ipAddress);
            redisTemplate.opsForValue().set(key, "1");
            redisTemplate.expire(key, VIEW_HISTORY_EXPIRE_MINUTES, TimeUnit.MINUTES);
            return true;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * 조회 이력이 존재하는지 확인합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 조회 이력 존재 여부
     * @throws NewsInfoException 처리 실패 시
     */
    private boolean checkViewHistory(String newsId, String ipAddress) {
        try {
            String key = createViewHistoryKey(newsId, ipAddress);
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * 현재 조회수를 조회합니다.
     *
     * @param newsId 뉴스 ID
     * @return 현재 조회수
     * @throws NewsInfoException 처리 실패 시
     */
    private Long retrieveCurrentViewCount(String newsId) {
        try {
            return getViewCountFromRedis(newsId)
                    .orElseGet(() -> getViewCountFromDb(newsId));
        } catch (NewsInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * Redis에서 뉴스의 현재 조회수를 조회합니다.
     */
    private Optional<Long> getViewCountFromRedis(String newsId) {
        try {
            String key = createViewCountKey(newsId);
            String countFromRedis = redisTemplate.opsForValue().get(key);

            return Optional.ofNullable(countFromRedis)
                    .map(this::parseViewCount);
        } catch (NewsInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * 조회수 문자열을 Long 타입으로 파싱합니다.
     */
    private Long parseViewCount(String countValue) {
        try {
            return Long.parseLong(countValue);
        } catch (NumberFormatException e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_INVALID_FORMAT, e);
        }
    }

    /**
     * DB에서 뉴스의 현재 조회수를 조회합니다.
     */
    private Long getViewCountFromDb(String newsId) {
        try {
            return newsInfoJpaRepository.findByGuid(newsId)
                    .stream()
                    .findFirst()
                    .map(article -> Objects.requireNonNullElse(article.getViewCount(), 0L))
                    .orElse(0L);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.NEWS_INFO_NOT_FOUND, e);
        }
    }

    /**
     * Redis에 조회수를 저장하고 만료 시간을 설정합니다.
     */
    private void saveViewCountToRedis(String key, Long count) {
        try {
            redisTemplate.opsForValue().set(key, String.valueOf(count));
            redisTemplate.expire(key, VIEW_COUNT_EXPIRE_DAYS, TimeUnit.DAYS);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * 뉴스 ID로 Redis 조회수 키를 생성합니다.
     */
    private String createViewCountKey(String newsId) {
        return VIEW_COUNT_KEY_PREFIX + newsId;
    }

    /**
     * 뉴스 ID와 IP 주소로 조회 이력 키를 생성합니다.
     */
    private String createViewHistoryKey(String newsId, String ipAddress) {
        return VIEW_HISTORY_KEY_PREFIX + newsId + ":" + ipAddress;
    }
}