package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util;

import org.springframework.stereotype.Component;

/**
 * Redis 키 생성을 담당하는 유틸리티 클래스입니다.
 *
 * 모든 Redis 키의 생성 규칙을 중앙화하여 일관성을 보장하고
 * 키 패턴 변경 시 한 곳에서만 수정할 수 있도록 합니다.
 *
 * @author 양병학
 * @since 2025-05-27 최초 작성
 */
@Component
public class RedisKeyGenerator {

    private static final String VIEW_COUNT_KEY_PREFIX = "news:viewCount:";
    private static final String VIEW_HISTORY_KEY_PREFIX = "news:viewHistory:";
    private static final String RANKING_KEY_PREFIX = "news:ranking:";
    private static final String HASH_KEY_PREFIX = "news:hash:";
    private static final String TOP_NEWS_KEY_PREFIX = "news:topNews:";

    /**
     * 뉴스 조회수 저장용 Redis 키를 생성합니다.
     *
     * @param newsId 뉴스 ID
     * @return Redis 키 (예: "news:viewCount:news123")
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    public String createViewCountKey(String newsId) {
        return VIEW_COUNT_KEY_PREFIX + newsId;
    }

    /**
     * 사용자 뉴스 조회 이력 저장용 Redis 키를 생성합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return Redis 키 (예: "news:viewHistory:news123:192.168.1.1")
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    public String createViewHistoryKey(String newsId, String ipAddress) {
        return VIEW_HISTORY_KEY_PREFIX + newsId + ":" + ipAddress;
    }

    /**
     * 카테고리별 뉴스 랭킹 저장용 Redis 키를 생성합니다.
     *
     * @param category 카테고리명
     * @return Redis 키 (예: "news:ranking:정치")
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    public String createRankingKey(String category) {
        return RANKING_KEY_PREFIX + category;
    }

    /**
     * 랭킹 해시값 저장용 Redis 키를 생성합니다.
     *
     * @param category 카테고리명
     * @return Redis 키 (예: "news:hash:정치")
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    public String createHashKey(String category) {
        return HASH_KEY_PREFIX + category;
    }

    /**
     * Top1 뉴스 캐시 저장용 Redis 키를 생성합니다.
     *
     * @param category 카테고리명
     * @return Redis 키 (예: "news:topNews:정치")
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    public String createTopNewsKey(String category) {
        return TOP_NEWS_KEY_PREFIX + category;
    }
}