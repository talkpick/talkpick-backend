// application/service/PopularNewsService.java
package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.PopularNewsResponseMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.PopularNewsUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.PopularNewsPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util.HashUtility;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@Service
@RequiredArgsConstructor
public class PopularNewsService implements PopularNewsUseCase {

	private final PopularNewsPort popularNewsPort;
	private final NewsDetailProviderPort newsDetailProviderPort;

	private static final List<String> CATEGORIES = Arrays.asList(
			"전체", "정치", "경제", "사회", "국제", "연예", "스포츠"
	);

	@Override
	public List<PopularNewsResponse> getTopNewsByAllCategories() {
		List<PopularNewsResponse> result = new ArrayList<>();

		for (String category : CATEGORIES) {
			if (hasRankingChanged(category)) {
				log.info("랭킹 변화 감지 - 카테고리: {}", category);
				evictCacheForCategory(category);
			}

			PopularNewsResponse topNews = getTopNewsByCategory(category);
			if (topNews != null) {
				result.add(topNews);
			}
		}

		return result;
	}

	private boolean hasRankingChanged(String category) {
		try {
			String currentHash = calculateCurrentRankingHash(category);
			String savedHash = popularNewsPort.getSavedRankingHash(category);

			boolean changed = !currentHash.equals(savedHash);

			if (changed) {
				popularNewsPort.saveRankingHash(category, currentHash);
				log.debug("해시값 업데이트 - 카테고리: {}, 이전: {}, 현재: {}",
						category, savedHash, currentHash);
			}

			return changed;
		} catch (Exception e) {
			log.warn("해시 비교 실패 - 카테고리: {}, 에러: {}", category, e.getMessage());
			return true;
		}
	}

	private String calculateCurrentRankingHash(String category) {
		String top1Data = popularNewsPort.getTop1NewsWithScore(category);
		return HashUtility.calculateRankingHash(top1Data);  // 수정됨
	}

	@Cacheable(value = "topNewsByCategory", key = "#category")
	public PopularNewsResponse getTopNewsByCategory(String category) {
		log.info("=== 실제 DB 조회 실행 - 카테고리: {} ===", category);
		try {
			String topNewsId = popularNewsPort.getTop1NewsId(category);

			if (topNewsId == null) {
				log.debug("Top1 뉴스 없음 - 카테고리: {}", category);
				return null;
			}

			NewsInfoDetail newsDetail = newsDetailProviderPort.getNewsInfoDetailsByArticleId(topNewsId);
			return PopularNewsResponseMapper.toResponse(newsDetail);

		} catch (Exception e) {
			log.error("Top1 뉴스 조회 실패 - 카테고리: {}, 에러: {}", category, e.getMessage());
			return null;
		}
	}

	@CacheEvict(value = "topNewsByCategory", key = "#category")
	public void evictCacheForCategory(String category) {
		log.debug("캐시 무효화 - 카테고리: {}", category);
	}
}