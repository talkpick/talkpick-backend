package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.CachePerformanceTracker;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.PopularNewsUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.PopularNewsPort;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util.HashUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 인기뉴스 조회 서비스 구현체입니다.
 * <p>
 * 순위 변화 감지와 캐시 전략을 관리하며, 실제 캐시 동작은 PopularNewsCacheService에 위임합니다.
 *
 * @since 2025-05-27
 */
@Service
@RequiredArgsConstructor
public class PopularNewsService implements PopularNewsUseCase {

    private final PopularNewsCacheService cacheService;
    private final PopularNewsPort popularNewsPort;

    /**
     * 특정 카테고리의 Top1 인기뉴스를 조회합니다.
     * <p>
     * 1. 해당 카테고리의 순위 변화 감지 (해시 비교)
     * 2. 변화가 감지되면 캐시 무효화 처리
     * 3. Spring Cache를 통해 인기뉴스 조회
     * 4. 조회된 결과를 반환
     *
     * @param categoryName 조회할 카테고리 한글명
     * @return 해당 카테고리의 Top1 인기뉴스
     * @throws NewsInfoException 뉴스 정보 조회 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    @CachePerformanceTracker
    public PopularNewsResponse getTopNewsByCategory(String categoryName) {

        boolean rankingChanged = hasRankingChanged(categoryName);

        if (rankingChanged) {
            cacheService.evictCache(categoryName);
        }

        PopularNewsResponse result = cacheService.getTopNewsWithCache(categoryName);

        return result;
    }

    /**
     * 순위 변화를 감지합니다.
     * <p>
     * 1. 현재 순위의 해시값 계산
     * 2. 저장된 해시값과 비교
     * 3. 변화가 있으면 새 해시값 저장 후 true 반환
     *
     * @param category 확인할 카테고리명 (한글)
     * @return 순위가 변화했으면 true, 그렇지 않으면 false
     * @author 양병학
     * @since 2025-05-27 최초 작성
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
     * @author 양병학
     * @since 2025-05-27 최초 작성
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
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    private boolean isHashChanged(String currentHash, String savedHash) {
        return !Objects.equals(currentHash, savedHash);
    }
}