package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.item.ViewCountItem;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.mapper.ViewCountMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ArticleEntity;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 조회수 업데이트 로직을 처리하는 서비스 클래스입니다.
 * 배치 처리를 통해 효율적으로 데이터베이스 접근을 최적화합니다.
 *
 * @since 2025-05-23
 * @modified 2025-05-24 엔티티 변환 로직을 Mapper로 분리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ViewCountUpdateService {

	private final NewsInfoJpaRepository repository;
	private final int batchSize = 100;

	/**
	 * 조회수 데이터를 일괄 업데이트합니다.
	 * 부분 조회 후 saveAll 방식으로 최적화되었습니다.
	 *
	 * @param items 업데이트할 조회수 항목 목록
	 * @return 업데이트된 항목 수
	 */
	@Transactional
	public int updateViewCounts(List<? extends ViewCountItem> items) {
		if (items.isEmpty()) {
			return 0;
		}

		// ViewCountItem을 Map으로 변환하여 참조 효율성 높임
		Map<String, Long> viewCountMap = createViewCountMap(items);

		// 모든 guid 수집
		List<String> allGuids = new ArrayList<>(viewCountMap.keySet());
		int totalUpdated = 0;

		// 배치 단위로 처리 (for문 유지)
		for (int i = 0; i < allGuids.size(); i += batchSize) {
			int end = Math.min(i + batchSize, allGuids.size());
			List<String> batchGuids = allGuids.subList(i, end);

			int batchUpdated = processBatch(batchGuids, viewCountMap);
			totalUpdated += batchUpdated;

			log.debug("배치 업데이트 완료: {}-{}, {}건 성공", i, end - 1, batchUpdated);
		}

		return totalUpdated;
	}

	/**
	 * 조회수 항목 목록에서 guid를 키로 하고 조회수를 값으로 하는 맵을 생성합니다.
	 *
	 * @param items 조회수 항목 목록
	 * @return 조회수 맵 (guid -> 조회수)
	 */
	private Map<String, Long> createViewCountMap(List<? extends ViewCountItem> items) {
		return items.stream()
			.collect(Collectors.toMap(
				ViewCountItem::getNewsId,
				ViewCountItem::getViewCount,
				(existing, replacement) -> replacement  // 중복 시 최신 값 사용
			));
	}

	/**
	 * 배치 단위로 조회수 업데이트를 처리합니다.
	 *
	 * @param guids 처리할 guid 목록
	 * @param viewCountMap 조회수 맵 (guid -> 조회수)
	 * @return 업데이트된 항목 수
	 */
	private int processBatch(List<String> guids, Map<String, Long> viewCountMap) {
		try {
			// 필요한 필드만 조회
			List<ViewCountItem> existingItems = repository.findViewCountItemsByGuidIn(guids);

			if (existingItems.isEmpty()) {
				return 0;
			}

			// 엔티티 변환 및 조회수 업데이트 (Mapper 활용)
			List<ArticleEntity> entitiesToUpdate = existingItems.stream()
				.map(item -> ViewCountMapper.toEntity(item, viewCountMap.get(item.getNewsId())))
				.collect(Collectors.toList());

			// 배치 저장
			if (!entitiesToUpdate.isEmpty()) {
				repository.saveAll(entitiesToUpdate);
				return entitiesToUpdate.size();
			}

			return 0;
		} catch (Exception e) {
			log.error("배치 처리 중 오류 발생: {}", e.getMessage());
			throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_BATCH_UPDATE_FAILED, e);
		}
	}
}