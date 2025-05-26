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

/**
 * NewsInfoProviderPort 구현체.
 * JPA 기반으로 뉴스 정보를 조회하며, 슬라이스 방식의 페이지네이션과 카테고리 필터링 기능을 제공한다.
 *
 * @since 2025-05-16
 * @modified 2025-05-26
 */
@Component
@RequiredArgsConstructor
public class NewsInfoProviderAdapter implements NewsInfoProviderPort {
	private final NewsInfoJpaRepository newsInfoJpaRepository;

	/**
	 * 마지막 뉴스 ID 기준으로 최신 뉴스 목록을 조회한다.
	 *
	 * @param lastNewsId 마지막 뉴스 ID (null이면 첫 페이지로 간주)
	 * @param limit 조회할 뉴스 개수
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-16
	 * @modified 2025-05-26
	 */
	@Override
	public SliceResult<NewsInfo> getLatestNewsInfo(String lastNewsId, int limit) {
		Pageable pageable = createPageable(limit);

		if (lastNewsId == null) {
			return getFirstArticles(pageable);
		}

		long articleId = getArticleIdByNewsId(lastNewsId);

		return getLatestArticles(articleId, pageable);
	}

	/**
	 * 마지막 뉴스 ID와 카테고리 기준으로 최신 뉴스 목록을 조회한다.
	 *
	 * @param inputCategory 입력된 카테고리명
	 * @param lastNewsId 마지막 뉴스 ID (null이면 첫 페이지로 간주)
	 * @param limit 조회할 뉴스 개수
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-26
	 */
	@Override
	public SliceResult<NewsInfo> getLatestNewsInfoByCategory(String inputCategory, String lastNewsId, int limit) {
		Pageable pageable = createPageable(limit);
		String category = NewsCategory.displayNameOf(inputCategory);

		if (lastNewsId == null) {
			return getFirstArticlesByCategory(category, pageable);
		}

		long articleId = getArticleIdByNewsId(lastNewsId);

		Slice<NewsInfo> slice = newsInfoJpaRepository.findAllByCategoryAndIdLessThanOrderByIdDesc
				(category, articleId, pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);

		return SliceResultBuilder.createSliceResult(slice);
	}

	/**
	 * 첫 페이지의 뉴스 목록을 조회한다.
	 *
	 * @param pageable 페이지 정보
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-26
	 */
	private SliceResult<NewsInfo> getFirstArticles(Pageable pageable) {
		Slice<NewsInfo> slice = newsInfoJpaRepository.findAllByOrderByIdDesc(pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);
		return SliceResultBuilder.createSliceResult(slice);
	}

	/**
	 * 첫 페이지의 카테고리별 뉴스 목록을 조회한다.
	 *
	 * @param category 카테고리명
	 * @param pageable 페이지 정보
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-26
	 */
	private SliceResult<NewsInfo> getFirstArticlesByCategory(String category, Pageable pageable) {
		Slice<NewsInfo> slice = newsInfoJpaRepository.findAllByCategoryOrderByIdDesc(category, pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);
		return SliceResultBuilder.createSliceResult(slice);
	}

	/**
	 * 마지막 뉴스 ID보다 작은 ID를 가진 최신 뉴스 목록을 조회한다.
	 *
	 * @param articleId 기준이 되는 뉴스 ID
	 * @param pageable 페이지 정보
	 * @return 뉴스 정보 슬라이스 결과
	 * @author 함예정
	 * @since 2025-05-26
	 */
	private SliceResult<NewsInfo> getLatestArticles(long articleId, Pageable pageable) {
		Slice<NewsInfo> slice = newsInfoJpaRepository.findAllByIdLessThanOrderByIdDesc(articleId, pageable)
			.map(ArticleEntityMapper::toInfoFromEntity);
		return SliceResultBuilder.createSliceResult(slice);
	}

	/**
	 * 정렬 기준을 포함한 Pageable 객체를 생성한다.
	 *
	 * @param limit 페이지 당 항목 수
	 * @return Pageable 객체
	 * @author 함예정
	 * @since 2025-05-26
	 */
	private Pageable createPageable(int limit) {
		Sort sortType = SortBuilder.createSortByIdDesc();
		return PageableBuilder.createPageable(0, limit, sortType);
	}

	/**
	 * 뉴스 ID(guid)를 기반으로 내부 article ID를 조회한다.
	 *
	 * @param newsId 뉴스 GUID
	 * @return 내부 article ID
	 * @throws NewsInfoException 뉴스 정보가 존재하지 않을 경우
	 * @author 함예정
	 * @since 2025-05-26
	 */
	private long getArticleIdByNewsId(String newsId) {
		try {
			return newsInfoJpaRepository.findByGuid(newsId)
				.getFirst()
				.getId();
		} catch (NoSuchElementException e) {
			throw new NewsInfoException(NewsInfoErrorCode.NEWS_INFO_NOT_FOUND);
		}
	}
}
