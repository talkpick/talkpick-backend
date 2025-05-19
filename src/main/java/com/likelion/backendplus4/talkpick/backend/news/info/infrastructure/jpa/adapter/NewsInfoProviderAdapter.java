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
	public SliceResult<NewsInfo> getLatestNewsInfo(Long lastId, int limit) {
		Sort sortType = SortBuilder.createSortByIdDesc();
		Pageable pageable = PageableBuilder.createPageable(0, limit, sortType);

		if (lastId == null) {
			return getFirstPageArticles(pageable);
		}
		return getNextPageArticles(lastId, pageable);
	}

	private SliceResult<NewsInfo> getFirstPageArticles(Pageable pageable) {
		Slice<NewsInfo> slice =
			newsInfoJpaRepository.findAll(pageable)
				.map(ArticleEntityMapper::toInfoFromEntity);

		return SliceResultBuilder.createSliceResult(slice);
	}

	private SliceResult<NewsInfo> getNextPageArticles(Long lastId, Pageable pageable) {
		Slice<NewsInfo> slice = newsInfoJpaRepository.findByIdLessThanOrderByIdDesc(lastId, pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);

		return SliceResultBuilder.createSliceResult(slice);
	}
}
