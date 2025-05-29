package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.adapter;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.PopularNewsPort;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util.RedisKeyGenerator;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util.RedisJsonConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 인기뉴스 랭킹 관리를 담당하는 Redis 어댑터입니다.
 *
 * 뉴스 랭킹 조회, 해시값 관리, Top1 뉴스 캐싱 등의 기능을 제공합니다.
 *
 * @author 양병학
 * @since 2025-05-27 최초 작성
 */
@Component
@RequiredArgsConstructor
public class PopularNewsRedisAdapter implements PopularNewsPort {

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisKeyGenerator keyGenerator;
    private final RedisJsonConverter jsonConverter;

    /**
     * 특정 카테고리의 Top1 뉴스 ID를 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return Top1 뉴스 ID, 없으면 null
     * @throws NewsInfoException Top1 뉴스 조회 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public String getTop1NewsId(String category) {
        return retrieveTop1NewsId(category);
    }

    /**
     * 특정 카테고리의 Top1 뉴스와 점수를 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return "뉴스ID:점수" 형태의 문자열, 없으면 "empty"
     * @throws NewsInfoException Top1 뉴스 조회 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public String getTop1NewsWithScore(String category) {
        return retrieveTop1NewsWithScore(category);
    }

    /**
     * 랭킹 해시값을 저장합니다.
     *
     * @param category  카테고리명
     * @param hashValue 저장할 해시값
     * @throws NewsInfoException 해시값 저장 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public void saveRankingHash(String category, String hashValue) {
        storeRankingHash(category, hashValue);
    }

    /**
     * 저장된 랭킹 해시값을 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return 저장된 해시값
     * @throws NewsInfoException 해시값 조회 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public String getSavedRankingHash(String category) {
        return retrieveSavedRankingHash(category);
    }

    /**
     * 카테고리별 Top1 뉴스 결과를 저장합니다.
     *
     * @param category 카테고리명
     * @param topNews  저장할 뉴스 데이터
     * @throws NewsInfoException 캐시 저장 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public void saveTopNews(String category, PopularNewsResponse topNews) {
        storeTopNewsToCache(category, topNews);
    }

    /**
     * 카테고리별 Top1 뉴스 결과를 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return 캐시된 뉴스 데이터, 없으면 null
     * @throws NewsInfoException 캐시 조회 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public PopularNewsResponse getTopNews(String category) {
        return retrieveTopNewsFromCache(category);
    }

    /**
     * 랭킹 점수를 업데이트합니다.
     *
     * @param category  카테고리명
     * @param newsId    뉴스 ID
     * @param viewCount 조회수
     * @throws NewsInfoException 랭킹 업데이트 중 오류가 발생한 경우
     */
    public void updateRankingScore(String category, String newsId, Long viewCount, LocalDateTime publishDate) {
        performRankingScoreUpdate(category, newsId, viewCount, publishDate);
    }

    /**
     * Top1 뉴스 ID를 조회합니다.
     *
     * @param category 카테고리명
     * @return Top1 뉴스 ID
     * @throws NewsInfoException 조회 실패 시
     */
    private String retrieveTop1NewsId(String category) {
        try {
            String rankingKey = keyGenerator.createRankingKey(category);
            Set<String> top1 = redisTemplate.opsForZSet().reverseRange(rankingKey, 0, 0);
            return top1.isEmpty() ? null : top1.iterator().next();
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.RANKING_TOP1_QUERY_FAILED, e);
        }
    }

    /**
     * Top1 뉴스와 점수를 조회합니다.
     *
     * @param category 카테고리명
     * @return "뉴스ID:점수" 형태의 문자열
     * @throws NewsInfoException 조회 실패 시
     */
    private String retrieveTop1NewsWithScore(String category) {
        try {
            String rankingKey = keyGenerator.createRankingKey(category);
            Set<ZSetOperations.TypedTuple<String>> top1WithScore =
                    redisTemplate.opsForZSet().reverseRangeWithScores(rankingKey, 0, 0);

            if (top1WithScore.isEmpty()) {
                return "empty";
            }

            ZSetOperations.TypedTuple<String> tuple = top1WithScore.iterator().next();
            return tuple.getValue() + ":" + tuple.getScore().longValue();
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.RANKING_TOP1_QUERY_FAILED, e);
        }
    }

    /**
     * 랭킹 해시값을 저장합니다.
     *
     * @param category  카테고리명
     * @param hashValue 해시값
     * @throws NewsInfoException 저장 실패 시
     */
    private void storeRankingHash(String category, String hashValue) {
        try {
            String hashKey = keyGenerator.createHashKey(category);
            redisTemplate.opsForValue().set(hashKey, hashValue);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.RANKING_HASH_SAVE_FAILED, e);
        }
    }

    /**
     * 저장된 랭킹 해시값을 조회합니다.
     *
     * @param category 카테고리명
     * @return 해시값
     * @throws NewsInfoException 조회 실패 시
     */
    private String retrieveSavedRankingHash(String category) {
        try {
            String hashKey = keyGenerator.createHashKey(category);
            return redisTemplate.opsForValue().get(hashKey);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.RANKING_HASH_RETRIEVAL_FAILED, e);
        }
    }

    /**
     * Top1 뉴스를 캐시에 저장합니다.
     *
     * @param category 카테고리명
     * @param topNews  뉴스 데이터
     * @throws NewsInfoException 저장 실패 시
     */
    private void storeTopNewsToCache(String category, PopularNewsResponse topNews) {
        try {
            String topNewsKey = keyGenerator.createTopNewsKey(category);
            String jsonValue = jsonConverter.toJson(topNews);
            redisTemplate.opsForValue().set(topNewsKey, jsonValue);
            redisTemplate.expire(topNewsKey, 1, TimeUnit.HOURS);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.CACHE_SAVE_FAILED, e);
        }
    }

    /**
     * 캐시에서 Top1 뉴스를 조회합니다.
     *
     * @param category 카테고리명
     * @return 뉴스 데이터
     * @throws NewsInfoException 조회 실패 시
     */
    private PopularNewsResponse retrieveTopNewsFromCache(String category) {
        try {
            String topNewsKey = keyGenerator.createTopNewsKey(category);
            String jsonValue = redisTemplate.opsForValue().get(topNewsKey);

            if (jsonValue == null) {
                return null;
            }

            return jsonConverter.fromJson(jsonValue, PopularNewsResponse.class);
        } catch (NewsInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.CACHE_RETRIEVE_FAILED, e);
        }
    }

    /**
     * 랭킹 점수를 업데이트합니다.
     *
     * @param category  카테고리명
     * @param newsId    뉴스 ID
     * @param viewCount 조회수
     * @throws NewsInfoException 업데이트 실패 시
     */
    private void performRankingScoreUpdate(String category, String newsId, Long viewCount, LocalDateTime publishDate) {
        try {
            String rankingKey = keyGenerator.createRankingKey(category, publishDate);
            redisTemplate.opsForZSet().add(rankingKey, newsId, viewCount.doubleValue());
            redisTemplate.expire(rankingKey, 3, TimeUnit.DAYS);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.RANKING_SCORE_UPDATE_FAILED, e);
        }
    }
}