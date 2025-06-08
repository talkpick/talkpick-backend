package com.likelion.backendplus4.talkpick.backend.news.info.application.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDynamic;

public class NewsInfoDynamicMapper {
    public static NewsInfoDynamic toNewsInfoDynamic(String newsId, Long viewCount) {
        return NewsInfoDynamic.builder()
                .newsId(newsId)
                .viewCount(viewCount)
                .build();
    }
}
