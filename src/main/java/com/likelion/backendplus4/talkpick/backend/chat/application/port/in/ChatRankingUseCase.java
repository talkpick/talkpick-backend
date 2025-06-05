package com.likelion.backendplus4.talkpick.backend.chat.application.port.in;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;

public interface ChatRankingUseCase {
    PopularNewsResponse getTopNewsByCategory(String category);

    PopularNewsResponse getTopNewsAll();
}