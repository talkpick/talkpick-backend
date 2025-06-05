package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatNewsProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.PopularNewsResponseMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatNewsProviderAdapter implements ChatNewsProviderPort {

    private final NewsInfoDetailProviderUseCase newsInfoDetailUseCase;

    @Override
    public PopularNewsResponse getNewsById(String newsId) {
        NewsInfoDetail newsDetail = newsInfoDetailUseCase.getNewsDetail(newsId);

        return (null == newsDetail)
                ? null
                : PopularNewsResponseMapper.toResponse(newsDetail);
    }
}