package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewsInfoViewCount {
    private final String newsId;
    private Long viewCount;
    private final LocalDateTime publishDate;
    private final String category;

    public void addViewCount() {
        this.viewCount++;
    }

    public boolean isEligibleForRanking() {
        return isRecentNews() && isValidViewCount();
    }

    private boolean isRecentNews() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        return publishDate.isAfter(threeDaysAgo);
    }

    private boolean isValidViewCount() {
        return viewCount != null && viewCount >= 0;
    }
}