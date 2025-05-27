package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.PopularNewsUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.PopularNewsPort;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util.HashUtility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PopularNewsService implements PopularNewsUseCase {

    private final PopularNewsCacheService cacheService;
    private final PopularNewsPort popularNewsPort;

    private static final List<String> CATEGORIES = Arrays.asList(
            "전체", "정치", "경제", "사회", "국제", "연예", "스포츠"
    );

    /**
     * 전체 카테고리의 인기뉴스를 조회합니다.
     *
     * 1. 각 카테고리별로 순위 변화 감지
     * 2. 변화가 감지되면 캐시 무효화 처리
     * 3. 캐시 서비스를 통해 카테고리별 인기뉴스 조회
     * 4. 조회된 결과를 리스트로 반환
     *
     * @return 전체 카테고리의 인기뉴스 목록
     * @throws NewsInfoException 뉴스 정보 조회 중 오류가 발생한 경우
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    @Override
    public List<PopularNewsResponse> getTopNewsByAllCategories() {
        return processAllCategories();
    }

    /**
     * 전체 카테고리를 순회하며 인기뉴스를 처리합니다.
     *
     * @return 처리된 인기뉴스 목록
     * @throws NewsInfoException 뉴스 정보 처리 중 오류가 발생한 경우
     */
    private List<PopularNewsResponse> processAllCategories() {
        List<PopularNewsResponse> result = new ArrayList<>();

        for (String category : CATEGORIES) {
            processCategory(category, result);
        }

        return result;
    }

    /**
     * 개별 카테고리의 인기뉴스를 처리합니다.
     *
     * @param category 처리할 카테고리명
     * @param result   결과를 담을 리스트
     * @throws NewsInfoException 카테고리 처리 중 오류가 발생한 경우
     */
    private void processCategory(String category, List<PopularNewsResponse> result) {
        try {
            handleCacheEvictionIfNeeded(category);
            addTopNewsToResult(category, result);
        } catch (NewsInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.POPULAR_NEWS_CATEGORY_PROCESSING_FAILED, e);
        }
    }

    /**
     * 순위 변화가 감지되면 캐시 무효화를 처리합니다.
     *
     * @param category 확인할 카테고리명
     * @throws NewsInfoException 캐시 무효화 처리 중 오류가 발생한 경우
     */
    private void handleCacheEvictionIfNeeded(String category) {
        try {
            if (hasRankingChanged(category)) {
                cacheService.evictCache(category);
            }
        } catch (NewsInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.POPULAR_NEWS_CACHE_EVICTION_FAILED, e);
        }
    }

    /**
     * 카테고리별 최고 인기뉴스를 결과 목록에 추가합니다.
     *
     * @param category 조회할 카테고리명
     * @param result   결과를 담을 리스트
     * @throws NewsInfoException 뉴스 조회 중 오류가 발생한 경우
     */
    private void addTopNewsToResult(String category, List<PopularNewsResponse> result) {
        PopularNewsResponse topNews = cacheService.getTopNewsByCategory(category);
        if (topNews != null) {
            result.add(topNews);
        }
    }

    /**
     * 순위 변화를 감지합니다.
     *
     * @param category 확인할 카테고리명
     * @return 순위가 변화했으면 true, 그렇지 않으면 false
     * @throws NewsInfoException 순위 변화 감지 중 오류가 발생한 경우
     */
    private boolean hasRankingChanged(String category) {
        String currentHash = calculateCurrentRankingHash(category);
        String savedHash = getSavedRankingHash(category);

        if (isHashChanged(currentHash, savedHash)) {
            updateRankingHash(category, currentHash);
            return true;
        }

        return false;
    }

    /**
     * 현재 순위 기반 해시값을 계산합니다.
     *
     * @param category 해시를 계산할 카테고리명
     * @return 계산된 해시값
     * @throws NewsInfoException 1위 뉴스 데이터 조회 실패 또는 해시 계산 실패한 경우
     */
    private String calculateCurrentRankingHash(String category) {
        try {
            String top1Data = popularNewsPort.getTop1NewsWithScore(category);
            if (top1Data == null || top1Data.trim().isEmpty()) {
                throw new NewsInfoException(NewsInfoErrorCode.POPULAR_NEWS_TOP1_NOT_FOUND);
            }
            return HashUtility.calculateRankingHash(top1Data);
        } catch (NewsInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.RANKING_HASH_CALCULATION_FAILED, e);
        }
    }

    /**
     * 저장된 순위 해시값을 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return 저장된 해시값
     * @throws NewsInfoException 해시값 조회 중 오류가 발생한 경우
     */
    private String getSavedRankingHash(String category) {
        try {
            return popularNewsPort.getSavedRankingHash(category);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.RANKING_HASH_RETRIEVAL_FAILED, e);
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

    /**
     * 순위 해시값을 업데이트합니다.
     *
     * @param category    업데이트할 카테고리명
     * @param currentHash 현재 해시값
     * @throws NewsInfoException 해시값 저장 중 오류가 발생한 경우
     */
    private void updateRankingHash(String category, String currentHash) {
        try {
            popularNewsPort.saveRankingHash(category, currentHash);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.RANKING_HASH_SAVE_FAILED, e);
        }
    }
}