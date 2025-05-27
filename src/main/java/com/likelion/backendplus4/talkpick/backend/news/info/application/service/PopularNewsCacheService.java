package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.PopularNewsPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PopularNewsCacheService {

    private final PopularNewsPort popularNewsPort;
    private final NewsDetailProviderPort newsDetailProviderPort;

    /**
     * Spring Cache를 통한 카테고리별 Top1 뉴스를 조회합니다.
     *
     * 1. Spring 캐시에서 조회 시도 (캐시 HIT 시 즉시 반환)
     * 2. 캐시 MISS 시 데이터 소스에서 조회
     * 3. Redis → DB 순으로 조회 후 캐시 저장
     * 4. 조회된 결과 반환
     *
     * @param category 조회할 카테고리명
     * @return 해당 카테고리의 최고 인기뉴스, 조회 실패 시 null
     * @throws NewsInfoException 뉴스 조회 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Cacheable(value = "popularNews", key = "#category")
    public PopularNewsResponse getTopNewsByCategory(String category) {
        return fetchTopNewsFromDataSource(category);
    }

    /**
     * 특정 카테고리의 Spring 캐시를 삭제합니다.
     *
     * @param category 캐시를 삭제할 카테고리명
     * @throws NewsInfoException 캐시 삭제 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @CacheEvict(value = "popularNews", key = "#category")
    public void evictCache(String category) {
    }

    /**
     * 데이터 소스에서 뉴스를 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return 조회된 인기뉴스, 없으면 null
     * @throws NewsInfoException 뉴스 조회 중 오류가 발생한 경우
     */
    private PopularNewsResponse fetchTopNewsFromDataSource(String category) {
        String topNewsId = getTop1NewsId(category);
        if (topNewsId == null) {
            return null;
        }

        PopularNewsResponse cachedNews = checkRedisCache(category, topNewsId);
        if (cachedNews != null) {
            return cachedNews;
        }

        return fetchFromDatabaseAndCache(category, topNewsId);
    }

    /**
     * 카테고리별 Top1 뉴스 ID를 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return Top1 뉴스 ID, 없으면 null
     * @throws NewsInfoException Top1 뉴스 ID 조회 중 오류가 발생한 경우
     */
    private String getTop1NewsId(String category) {
        try {
            return popularNewsPort.getTop1NewsId(category);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.POPULAR_NEWS_TOP1_NOT_FOUND, e);
        }
    }

    /**
     * Redis 캐시에서 뉴스를 확인합니다.
     *
     * @param category  조회할 카테고리명
     * @param topNewsId 확인할 뉴스 ID
     * @return 캐시된 뉴스 데이터, 캐시 미스 시 null
     * @throws NewsInfoException Redis 캐시 조회 중 오류가 발생한 경우
     */
    private PopularNewsResponse checkRedisCache(String category, String topNewsId) {
        try {
            PopularNewsResponse cachedNews = popularNewsPort.getTopNews(category);
            if (isCacheValid(cachedNews, topNewsId)) {
                return cachedNews;
            }
            return null;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.POPULAR_NEWS_CACHE_EVICTION_FAILED, e);
        }
    }

    /**
     * 캐시된 뉴스의 유효성을 검증합니다.
     *
     * @param cachedNews 캐시된 뉴스 데이터
     * @param topNewsId  현재 Top1 뉴스 ID
     * @return 유효하면 true, 그렇지 않으면 false
     */
    private boolean isCacheValid(PopularNewsResponse cachedNews, String topNewsId) {
        return cachedNews != null && Objects.equals(topNewsId, cachedNews.guid());
    }

    /**
     * 데이터베이스에서 뉴스를 조회하고 Redis 캐시에 저장합니다.
     *
     * @param category  조회할 카테고리명
     * @param topNewsId 조회할 뉴스 ID
     * @return 조회된 뉴스 데이터
     * @throws NewsInfoException 데이터베이스 조회 또는 캐시 저장 중 오류가 발생한 경우
     */
    private PopularNewsResponse fetchFromDatabaseAndCache(String category, String topNewsId) {
        PopularNewsResponse freshNews = getPopularNewsFromDatabase(topNewsId);

        if (freshNews != null) {
            saveToRedisCache(category, freshNews);
        }

        return freshNews;
    }

    private PopularNewsResponse getPopularNewsFromDatabase(String topNewsId) {
        try {
            return newsDetailProviderPort.getPopularNewsByArticleId(topNewsId);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.NEWS_INFO_NOT_FOUND, e);
        }
    }

    /**
     * Redis 캐시에 뉴스 데이터를 저장합니다.
     *
     * @param category  저장할 카테고리명
     * @param freshNews 저장할 뉴스 데이터
     * @throws NewsInfoException Redis 캐시 저장 중 오류가 발생한 경우
     */
    private void saveToRedisCache(String category, PopularNewsResponse freshNews) {
        try {
            popularNewsPort.saveTopNews(category, freshNews);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.POPULAR_NEWS_CACHE_EVICTION_FAILED, e);
        }
    }
}