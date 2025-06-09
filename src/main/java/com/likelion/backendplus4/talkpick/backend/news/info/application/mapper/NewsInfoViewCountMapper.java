package com.likelion.backendplus4.talkpick.backend.news.info.application.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoViewCount;

/**
 * ID와 조회수 입력을 전송하기 위한 객체로 매핑
 *
 * @since 2025-06-08
 */
public class NewsInfoViewCountMapper {

    /**
     * 개별 데이터를 응답 DTO로 변환합니다.
     *
     * @param newsId 뉴스 ID
     * @param viewCount 조회수
     * @return NewsInfoViewCount
     * @author 양병학
     * @since 2025-06-08
     */
    public static NewsInfoViewCount toNewsInfoViewCount(String newsId, Long viewCount) {
        return NewsInfoViewCount.builder()
                .newsId(newsId)
                .viewCount(viewCount)
                .build();
    }
}
