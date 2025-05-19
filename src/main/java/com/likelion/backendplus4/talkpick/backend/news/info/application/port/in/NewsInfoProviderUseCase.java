package com.likelion.backendplus4.talkpick.backend.news.info.application.port.in;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;

public interface NewsInfoProviderUseCase {
	NewsInfoDetailResponse getLatestNewsInfo(int lastIndex, int pageSize);
}
