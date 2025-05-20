package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.adapter;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.application.port.out.NewsViewCountPort;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;
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
     * @param newsId 조회수를 증가시킬 뉴스의 ID
     * @return 증가된 후의 조회수 값
     * @throws NewsInfoException Redis 작업 실패 시
     */
    @Override
    public Long increaseViewCount(String newsId, String ipAddress) {
        try {
            String key = createViewCountKey(newsId);
            String currentCount = redisTemplate.opsForValue().get(key);

            Long newCount = currentCount == null
                    ? handleColdData(key, newsId)
                    : handleHotData(key, newsId);

            saveViewHistory(newsId, ipAddress);

            return newCount;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * Redis에 뉴스 조회수 정보가 없을 때 초기화하는 메서드입니다.
     *
     * @param key    Redis에 저장할 키 값
     * @param newsId 뉴스 ID
     * @return 증가된 후의 조회수 값
     * @throws NewsInfoException DB 조회 또는 Redis 저장 실패 시
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
     * Redis에서 조회수 직접 증가
     *
     * @param key    Redis에 저장된 키 값
     * @param newsId 뉴스 ID (미사용 파라미터이지만 일관성을 위해 유지)
     * @return 증가된 후의 조회수 값
     * @throws NewsInfoException Redis 증가 작업 실패 시
     */
    private Long handleHotData(String key, String newsId) {
        try {
            return redisTemplate.opsForValue().increment(key);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * 사용자의 뉴스 조회 이력을 저장합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 저장 성공 여부
     * @throws NewsInfoException Redis 저장 실패 시
     */
    @Override
    public boolean saveViewHistory(String newsId, String ipAddress) {
        try {
            String key = createViewHistoryKey(newsId, ipAddress);
            redisTemplate.opsForValue().set(key, "1");
            redisTemplate.expire(key, VIEW_HISTORY_EXPIRE_DAYS, TimeUnit.DAYS);
            return true;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * 사용자가 특정 뉴스를 이미 조회했는지 확인합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 조회 이력 존재 여부
     * @throws NewsInfoException Redis 조회 실패 시
     */
    @Override
    public boolean hasViewHistory(String newsId, String ipAddress) {
        try {
            String key = createViewHistoryKey(newsId, ipAddress);
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }

    /**
     * 특정 뉴스의 현재 조회수를 조회합니다.
     *
     * @param newsId 조회할 뉴스의 ID
     * @return 현재 조회수
     * @throws NewsInfoException 조회 실패 시
     */
    @Override
    public Long getCurrentViewCount(String newsId) {
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
     *
     * @param newsId 뉴스 ID
     * @return 조회수를 담은 Optional (Redis에 없거나 파싱 오류 시 빈 Optional)
     * @throws NewsInfoException Redis 조회 실패 시
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
     * DB에서 뉴스의 현재 조회수를 조회합니다.
     *
     * @param newsId 뉴스 ID (GUID)
     * @return 현재 조회수 (없을 경우 0)
     * @throws NewsInfoException DB 조회 실패 시
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
     *
     * @param key   Redis 키
     * @param count 저장할 조회수
     * @throws NewsInfoException Redis 저장 실패 시
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
     *
     * @param newsId 뉴스 ID
     * @return Redis 키
     */
    private String createViewCountKey(String newsId) {
        return VIEW_COUNT_KEY_PREFIX + newsId;
    }

    /**
     * 뉴스 ID와 IP 주소로 조회 이력 키를 생성합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return Redis 키
     */
    private String createViewHistoryKey(String newsId, String ipAddress) {
        return VIEW_HISTORY_KEY_PREFIX + newsId + ":" + ipAddress;
    }
}