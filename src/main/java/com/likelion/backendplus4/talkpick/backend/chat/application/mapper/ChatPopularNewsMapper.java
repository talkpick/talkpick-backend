package com.likelion.backendplus4.talkpick.backend.chat.application.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.PopularNewsResponseMapper;

public class ChatPopularNewsMapper {

    public static PopularNewsResponse toPopularNewsResponse(String newsId,
        NewsInfoDetailProviderUseCase newsInfoDetailUseCase) {

        NewsInfoDetail newsDetail = newsInfoDetailUseCase.getNewsDetail(newsId);

        return (newsDetail == null)
                ? null
                : PopularNewsResponseMapper.toResponse(newsDetail);
    }
}