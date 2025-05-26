package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter;

import java.util.NoSuchElementException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsInfoProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.dto.NewsCategory;
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
	public SliceResult<NewsInfo> getLatestNewsInfo(String lastNewsId, int limit) {
		Pageable pageable = createPageable(limit);

		if(lastNewsId == null){
			return getFirstArticles(pageable);
		}

		long articleId = getArticleIdByNewsId(lastNewsId);

		return getLatestArticles(articleId, pageable);
	}

	@Override
	public SliceResult<NewsInfo> getLatestNewsInfoByCategory(String inputCategory, String lastNewsId, int limit) {
		Pageable pageable = createPageable(limit);
		String category = NewsCategory.displayNameOf(inputCategory);
		if(lastNewsId == null){
			return getFirstArticlesByCategory(category, pageable);
		}

		long articleId = getArticleIdByNewsId(lastNewsId);

		Slice<NewsInfo> slice = newsInfoJpaRepository.findAllByCategoryAndIdLessThanOrderByIdDesc
				(category, articleId, pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);

		return SliceResultBuilder.createSliceResult(slice);
	}

	private SliceResult<NewsInfo> getFirstArticles(Pageable pageable) {
		Slice<NewsInfo> slice = newsInfoJpaRepository.findAllByOrderByIdDesc(pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);
		return SliceResultBuilder.createSliceResult(slice);
	}

	private SliceResult<NewsInfo> getFirstArticlesByCategory(String category, Pageable pageable) {
		Slice<NewsInfo> slice = newsInfoJpaRepository.findAllByCategoryOrderByIdDesc(category, pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);
		return SliceResultBuilder.createSliceResult(slice);
	}

	private SliceResult<NewsInfo> getLatestArticles(long articleId, Pageable pageable) {
		Slice<NewsInfo> slice = newsInfoJpaRepository.findAllByIdLessThanOrderByIdDesc(articleId, pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);
		return SliceResultBuilder.createSliceResult(slice);
	}

	private Pageable createPageable(int limit) {
		Sort sortType = SortBuilder.createSortByIdDesc();
		return PageableBuilder.createPageable(0, limit, sortType);
	}

	private long getArticleIdByNewsId(String newsId) {
		try {
			return newsInfoJpaRepository.findByGuid(newsId)
				.getFirst()
				.getId();
		} catch (NoSuchElementException e){
			throw new NewsInfoException(NewsInfoErrorCode.NEWS_INFO_NOT_FOUND);
		}
	}
}
