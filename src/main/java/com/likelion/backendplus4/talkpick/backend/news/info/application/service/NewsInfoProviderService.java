package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsInfoProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NewsInfoProviderService implements NewsInfoProviderUseCase {
	private final NewsInfoProviderPort newsInfoProviderPort;

	@Override
	public SliceResult<NewsInfo> getLatestNewsInfo(String lastId, int pageSize) {
		return newsInfoProviderPort.getLatestNewsInfo(lastId, pageSize);
	}

	@Override
	public SliceResult<NewsInfo> getLatestNewsInfoByCategory(String category, String lastId, int pageSize) {
		return newsInfoProviderPort.getLatestNewsInfoByCategory(category, lastId, pageSize);
	}
}
