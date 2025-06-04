package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;

public interface ChatNewsProviderPort {
    PopularNewsResponse getNewsById(String newsId);
}