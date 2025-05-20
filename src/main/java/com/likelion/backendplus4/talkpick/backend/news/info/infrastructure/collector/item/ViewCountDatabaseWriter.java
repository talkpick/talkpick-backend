package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.item;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.service.ViewCountUpdateService;

import lombok.extern.slf4j.Slf4j;

/**
 * 처리된 조회수 데이터를 데이터베이스에 저장하는 ItemWriter 구현 클래스입니다.
 * 실제 업데이트 로직은 ViewCountUpdateService에 위임합니다.
 *
 * @since 2025-05-20
 * @modified 2025-05-23 서비스 레이어로 업데이트 로직 분리
 */
@Slf4j
public class ViewCountDatabaseWriter implements ItemWriter<ViewCountItem> {

    private final ViewCountUpdateService updateService;

    /**
     * 업데이트 서비스를 받아 Writer를 초기화합니다.
     *
     * @param updateService 조회수 업데이트 서비스
     */
    public ViewCountDatabaseWriter(ViewCountUpdateService updateService) {
        this.updateService = updateService;
    }

    /**
     * 처리된 조회수 항목들을 데이터베이스에 일괄 저장합니다.
     * Spring Batch가 호출하는 진입점 메서드입니다.
     *
     * @param items 저장할 조회수 항목 목록
     */
    @Override
    @Transactional
    public void write(Chunk<? extends ViewCountItem> items) {
        if (items.isEmpty()) {
            log.debug("처리할 조회수 항목이 없습니다.");
            return;
        }

        try {
            int updatedCount = updateService.updateViewCounts(items.getItems());
            log.info("조회수 업데이트 완료: 총 {}개 항목", updatedCount);
        } catch (Exception e) {
            log.error("조회수 업데이트 실패: {}", e.getMessage());
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }
}