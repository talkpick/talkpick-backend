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
	private static final int FIRST_PAGE_INDEX = -1;

	@Override
	public SliceResult<NewsInfo> getLatestNewsInfo(int page, int pageSize) {
		return newsInfoProviderPort.getLatestNewsInfo(page, pageSize);
	}
}
