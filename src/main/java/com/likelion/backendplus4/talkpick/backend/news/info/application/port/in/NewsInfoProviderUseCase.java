package com.likelion.backendplus4.talkpick.backend.news.info.application.port.in;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;

public interface NewsInfoProviderUseCase {
	SliceResult<NewsInfo> getLatestNewsInfo(String lastId, int pageSize);
	SliceResult<NewsInfo> getLatestNewsInfoByCategory(String category, String lastId, int pageSize);
}
