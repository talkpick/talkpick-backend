package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
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
 * NewsDetailProviderPort 인터페이스의 구현체로,
 * JPA를 통해 뉴스 상세 정보를 조회하는 어댑터 클래스입니다.
 *
 * @since 2025-05-14
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsInfoDetailProviderAdapter implements NewsDetailProviderPort {
	private final NewsInfoJpaRepository newsInfoJpaRepository;
	
	/**
	 * 주어진 guid(뉴스 ID)를 기준으로 뉴스 상세 정보를 조회합니다.
	 * 조회된 뉴스가 정확히 하나인 경우에만 도메인 객체로 변환하여 반환합니다.
	 *
	 * @param guid 뉴스의 고유 식별자
	 * @return 뉴스 상세 도메인 객체
	 * @author 함예정
	 * @since 2025-05-14
	 */
	@Override
	public NewsInfoDetail getNewsInfoDetailsByArticleId(String guid) {
		List<ArticleEntity> savedNewsInfo = newsInfoJpaRepository.findByGuid(guid);
		ArticleEntity entity = getOnlyArticleOrThrow(savedNewsInfo);
		return ArticleEntityMapper.toDetailFromEntity(entity);
	}

	/**
	 * 조회된 뉴스가 정확히 하나인지 검증하고, 단일 엔티티를 반환합니다.
	 *
	 * @param savedNewsInfo guid 기준으로 조회된 뉴스 리스트
	 * @return 단일 뉴스 엔티티
	 * @throws NewsInfoException 조회된 뉴스가 1건이 아닌 경우
	 * @author 함예정
	 * @since 2025-05-14
	 */
	private ArticleEntity getOnlyArticleOrThrow(List<ArticleEntity> savedNewsInfo) {

		if(savedNewsInfo.size() == 1) {
			return savedNewsInfo.getFirst();
		}
		throw new NewsInfoException(NewsInfoErrorCode.NON_UNIQUE_NEWS_INFO);
	}
}
