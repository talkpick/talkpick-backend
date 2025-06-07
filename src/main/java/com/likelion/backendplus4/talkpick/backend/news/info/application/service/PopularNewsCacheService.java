package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import org.springframework.stereotype.Service;

import java.util.Objects;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.PopularNewsPort;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * 인기뉴스 캐시 관리 전용 도메인 서비스입니다.
 * <p>
 * JVM 레벨 캐시(Spring Cache)와 Redis 캐시를 관리합니다.
 *
 * @since 2025-06-03
 */
@Service
@RequiredArgsConstructor
public class PopularNewsCacheService {

    private final PopularNewsPort popularNewsPort;

    /**
     * Spring Cache를 통한 카테고리별 Top1 뉴스를 조회합니다.
     * <p>
     * 1. Spring Cache에서 먼저 조회
     * 2. 캐시 미스 시 Redis → DB 순으로 조회
     * 3. 조회된 결과를 Spring Cache에 저장
     *
     * @param category 조회할 카테고리명
     * @return 해당 카테고리의 최고 인기뉴스, 조회 실패 시 null
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    @Cacheable(value = "popularNews", key = "#category")
    public PopularNewsResponse getTopNewsWithCache(String category) {
        return fetchTopNewsFromDataSource(category);
    }

    /**
     * 특정 카테고리의 Spring 캐시를 삭제합니다.
     *
     * @param category 캐시를 삭제할 카테고리명
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    @CacheEvict(value = "popularNews", key = "#category")
    public void evictCache(String category) {
    }

    /**
     * 데이터 소스에서 뉴스를 조회합니다.
     * <p>
     * 1. Redis 캐시에서 조회 시도
     * 2. Redis 캐시 미스 시 DB에서 조회
     * 3. DB 조회 결과를 Redis에 저장
     *
     * @param category 조회할 카테고리명
     * @return 조회된 인기뉴스, 없으면 null
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private PopularNewsResponse fetchTopNewsFromDataSource(String category) {
        String topNewsId = popularNewsPort.getTop1NewsId(category);

        if (null == topNewsId) {
            return null;
        }

        PopularNewsResponse cachedNews = checkRedisCache(category, topNewsId);

        if (null != cachedNews) {
            return cachedNews;
        }

        return fetchFromDatabaseAndCache(category, topNewsId);
    }

    /**
     * Redis 캐시에서 뉴스를 확인합니다.
     *
     * @param category  조회할 카테고리명
     * @param topNewsId 확인할 뉴스 ID
     * @return 캐시된 뉴스 데이터, 캐시 미스 시 null
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private PopularNewsResponse checkRedisCache(String category, String topNewsId) {
        PopularNewsResponse cachedNews = popularNewsPort.getTopNews(category);

        if (isCacheValid(cachedNews, topNewsId)) {
            return cachedNews;
        }

        return null;
    }

    /**
     * 캐시된 뉴스의 유효성을 검증합니다.
     *
     * @param cachedNews 캐시된 뉴스 데이터
     * @param topNewsId  현재 Top1 뉴스 ID
     * @return 유효하면 true, 그렇지 않으면 false
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private boolean isCacheValid(PopularNewsResponse cachedNews, String topNewsId) {
        return null != cachedNews && Objects.equals(topNewsId, cachedNews.guid());
    }

    /**
     * 데이터베이스에서 뉴스를 조회하고 Redis 캐시에 저장합니다.
     *
     * @param category  조회할 카테고리명
     * @param topNewsId 조회할 뉴스 ID
     * @return 조회된 뉴스 데이터
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private PopularNewsResponse fetchFromDatabaseAndCache(String category, String topNewsId) {
        PopularNewsResponse freshNews = popularNewsPort.getPopularNewsResponseById(topNewsId);

        if (null != freshNews) {
            popularNewsPort.saveTopNews(category, freshNews);
        }

        return freshNews;
    }
}
