package com.likelion.backendplus4.talkpick.backend.news.info.application.port.out;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;

public interface NewsInfoProviderPort {
	SliceResult<NewsInfo> getLatestNewsInfo(Long lastId, int limit);
}
