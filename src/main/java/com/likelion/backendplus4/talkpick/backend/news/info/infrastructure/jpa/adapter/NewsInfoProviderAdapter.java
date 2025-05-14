package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ArticleEntity;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.mapper.ArticleEntityMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;

import lombok.RequiredArgsConstructor;

/**
 * TODO: 이벤트 기반으로 색인 안된 뉴스만 제공하도록 수정 필요
 *  NewsDetailProviderPort 인터페이스의 구현체로,
 *  JPA 리포지토리를 통해 뉴스 정보를 조회하는 어댑터 클래스입니다.
 *  현재는 최근 100개 뉴스를 반환합니다.
 * @since 2025-05-14
 */
@Component
@RequiredArgsConstructor
public class NewsInfoProviderAdapter implements NewsDetailProviderPort {
	private final NewsInfoJpaRepository newsInfoJpaRepository;

	@Override
	public NewsInfoDetail getNewsInfoDetailsByArticleId(String guid) {
		List<ArticleEntity> savedNewsInfo = newsInfoJpaRepository.findByGuid(guid);
		ArticleEntity entity = getOnlyArticleOrThrow(savedNewsInfo);
		return ArticleEntityMapper.toDetailFromEntity(entity);
	}

	private ArticleEntity getOnlyArticleOrThrow(List<ArticleEntity> savedNewsInfo) {
		if(savedNewsInfo.size() == 1) {
			return savedNewsInfo.getFirst();
		}
		throw new NewsInfoException(NewsInfoErrorCode.NON_UNIQUE_NEWS_INFO);
	}
}
