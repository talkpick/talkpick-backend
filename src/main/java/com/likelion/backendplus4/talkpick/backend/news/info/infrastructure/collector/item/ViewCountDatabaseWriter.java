package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.item;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;

/**
 * 처리된 조회수 데이터를 데이터베이스에 저장하는 ItemWriter 구현 클래스입니다.
 *
 * @since 2025-05-20
 */
public class ViewCountDatabaseWriter implements ItemWriter<ViewCountItem> {

    private final NewsInfoJpaRepository repository;

    /**
     * JPA 리포지토리를 받아 Writer를 초기화합니다.
     *
     * @param repository 뉴스 정보 JPA 리포지토리
     */
    public ViewCountDatabaseWriter(NewsInfoJpaRepository repository) {
        this.repository = repository;
    }

    /**
     * 처리된 조회수 항목들을 데이터베이스에 저장합니다.
     *
     * @param items 저장할 조회수 항목 목록
     * @throws NewsInfoException DB 업데이트 실패 시
     */
    @Override
    public void write(Chunk<? extends ViewCountItem> items) {
        try {
            for (ViewCountItem item : items) {
                repository.findByGuid(item.getNewsId())
                        .stream()
                        .findFirst()
                        .ifPresent(article -> {
                            article.setViewCount(item.getViewCount());
                            repository.save(article);
                        });
            }
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_UPDATE_FAILED, e);
        }
    }
}