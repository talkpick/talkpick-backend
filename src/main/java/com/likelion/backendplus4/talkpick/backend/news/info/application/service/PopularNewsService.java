package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.CachePerformanceTracker;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.PopularNewsUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.PopularNewsPort;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util.HashUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 인기뉴스 조회 서비스 구현체입니다.
 *
 * 순위 변화 감지, 캐시 관리, 뉴스 조회를 통합 관리합니다.
 *
 * @since 2025-05-27
 */
@Service
@RequiredArgsConstructor
public class PopularNewsService implements PopularNewsUseCase {

    private final PopularNewsPort popularNewsPort;

    /**
     * 특정 카테고리의 Top1 인기뉴스를 조회합니다.
     *
     * 1. 해당 카테고리의 순위 변화 감지 (해시 비교)
     * 2. 변화가 감지되면 캐시 무효화 처리
     * 3. Spring Cache를 통해 인기뉴스 조회
     * 4. 조회된 결과를 반환
     *
     * @param categoryName 조회할 카테고리 한글명
     * @return 해당 카테고리의 Top1 인기뉴스
     * @throws NewsInfoException 뉴스 정보 조회 중 오류가 발생한 경우
     */
    @Override
    @CachePerformanceTracker
    public PopularNewsResponse getTopNewsByCategory(String categoryName) {
        if (hasRankingChanged(categoryName)) {
            evictCache(categoryName);
        }
        return getTopNewsWithCache(categoryName);
    }

    /**
     * Spring Cache를 통한 카테고리별 Top1 뉴스를 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return 해당 카테고리의 최고 인기뉴스, 조회 실패 시 null
     */
    @Cacheable(value = "popularNews", key = "#category")
    public PopularNewsResponse getTopNewsWithCache(String category) {
        return fetchTopNewsFromDataSource(category);
    }

    /**
     * 특정 카테고리의 Spring 캐시를 삭제합니다.
     *
     * @param category 캐시를 삭제할 카테고리명
     */
    @CacheEvict(value = "popularNews", key = "#category")
    public void evictCache(String category) {
    }

    /**
     * 데이터 소스에서 뉴스를 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return 조회된 인기뉴스, 없으면 null
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
     */
    private PopularNewsResponse fetchFromDatabaseAndCache(String category, String topNewsId) {
        PopularNewsResponse freshNews = popularNewsPort.getPopularNewsResponseById(topNewsId);

        if (null != freshNews) {
            popularNewsPort.saveTopNews(category, freshNews);
        }

        return freshNews;
    }

    /**
     * 순위 변화를 감지합니다.
     *
     * @param category 확인할 카테고리명 (한글)
     * @return 순위가 변화했으면 true, 그렇지 않으면 false
     */
    private boolean hasRankingChanged(String category) {
        String currentHash = calculateCurrentRankingHash(category);
        String savedHash = popularNewsPort.getSavedRankingHash(category);

        if (isHashChanged(currentHash, savedHash)) {
            popularNewsPort.saveRankingHash(category, currentHash);
            return true;
        }

        return false;
    }

    /**
     * 현재 순위 기반 해시값을 계산합니다.
     *
     * @param category 해시를 계산할 카테고리명 (한글)
     * @return 계산된 해시값
     */
    private String calculateCurrentRankingHash(String category) {
        String top1Data = popularNewsPort.getTop1NewsWithScore(category);
        validateTop1Data(top1Data);
        return HashUtility.calculateRankingHash(top1Data);
    }

    private void validateTop1Data(String top1Data) {
        if (null == top1Data || top1Data.trim().isEmpty()) {
            throw new NewsInfoException(NewsInfoErrorCode.POPULAR_NEWS_TOP1_NOT_FOUND);
        }
    }

    /**
     * 해시값 변화 여부를 확인합니다.
     *
     * @param currentHash 현재 해시값
     * @param savedHash   저장된 해시값
     * @return 변화했으면 true, 그렇지 않으면 false
     */
    private boolean isHashChanged(String currentHash, String savedHash) {
        return !Objects.equals(currentHash, savedHash);
    }
}