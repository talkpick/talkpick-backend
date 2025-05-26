package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsInfoProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.mapper.ArticleEntityMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.support.PageableBuilder;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.support.SliceResultBuilder;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.support.SortBuilder;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NewsInfoProviderAdapter implements NewsInfoProviderPort {
	private final NewsInfoJpaRepository newsInfoJpaRepository;

	@Override
	public SliceResult<NewsInfo> getLatestNewsInfo(int page, int limit) {
		return getArticles(createPageable(page, limit));
	}

	private SliceResult<NewsInfo> getArticles(Pageable pageable) {
		Slice<NewsInfo> slice = newsInfoJpaRepository.findAll(pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);
		return SliceResultBuilder.createSliceResult(slice);
	}

	private Pageable createPageable(int page, int limit) {
		Sort sortType = SortBuilder.createSortByIdDesc();
		return PageableBuilder.createPageable(page, limit, sortType);
	}
}
