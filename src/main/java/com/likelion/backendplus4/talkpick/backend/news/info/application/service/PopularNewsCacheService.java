// 🗄️ 캐시 전담 서비스
package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.PopularNewsResponseMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.PopularNewsPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularNewsCacheService {

    private final PopularNewsPort popularNewsPort;
    private final NewsDetailProviderPort newsDetailProviderPort;

    /**
     * Spring Cache를 통한 카테고리별 Top1 뉴스 조회
     * 캐시 HIT: 즉시 반환
     * 캐시 MISS: Redis → DB 순으로 조회 후 캐시 저장
     */
    @Cacheable(value = "popularNews", key = "#category")
    public PopularNewsResponse getTopNewsByCategory(String category) {
        log.info("📋 Spring 캐시 MISS - 새로 조회 시작: {}", category);

        PopularNewsResponse result = fetchFromDataSource(category);

        log.info("💾 Spring 캐시 저장 완료: {}", category);
        return result;
    }

    /**
     * 특정 카테고리의 Spring 캐시 삭제
     */
    @CacheEvict(value = "popularNews", key = "#category")
    public void evictCache(String category) {
        log.info("🗑️ Spring 캐시 삭제 - 카테고리: {}", category);
    }

    /**
     * 데이터 소스에서 뉴스 조회 (Redis → DB 순서)
     * 3단계 캐싱 구조의 2, 3단계 담당
     */
    private PopularNewsResponse fetchFromDataSource(String category) {
        try {
            // SortedSet에서 Top1 뉴스 ID 조회
            String topNewsId = popularNewsPort.getTop1NewsId(category);
            if (topNewsId == null) {
                log.debug("❌ Top1 뉴스 없음 - 카테고리: {}", category);
                return null;
            }

            log.info("🔍 Top1 뉴스 ID: {} (카테고리: {})", topNewsId, category);

            // 2단계: Redis topNews 캐시 확인
            PopularNewsResponse cachedNews = popularNewsPort.getTopNews(category);
            if (cachedNews != null && topNewsId.equals(cachedNews.guid())) {
                log.info("⚡ Redis topNews 캐시 HIT - 카테고리: {}", category);
                return cachedNews;
            }

            // 3단계: Redis MISS → DB 조회
            log.info("🔍 Redis topNews 캐시 MISS - DB 조회 시작: {}", category);
            NewsInfoDetail newsDetail = newsDetailProviderPort.getNewsInfoDetailsByArticleId(topNewsId);
            PopularNewsResponse freshNews = PopularNewsResponseMapper.toResponse(newsDetail);

            // Redis 캐시에 저장
            if (freshNews != null) {
                popularNewsPort.saveTopNews(category, freshNews);
                log.info("💾 Redis topNews 캐시 저장 완료 - 카테고리: {}", category);
            }

            return freshNews;

        } catch (Exception e) {
            log.error("❌ 뉴스 조회 실패 - 카테고리: {}, 에러: {}", category, e.getMessage());
            return null;
        }
    }
}