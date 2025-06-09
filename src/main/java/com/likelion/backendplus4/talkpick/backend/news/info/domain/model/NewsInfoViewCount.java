package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewsInfoViewCount {
    private final String newsId;
    private final Long viewCount;
}