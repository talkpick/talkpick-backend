package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter;

import static com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.mapper.ScrapEntityMapper.*;

import java.util.Optional;

import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.news.info.application.command.ScrapCommand;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.mapper.ArticleEntityMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.NewsInfoJpaRepository;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.repository.ScrapInfoJpaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * NewsDetailProviderPort 인터페이스의 구현체로,
 * JPA를 통해 뉴스 상세 정보를 조회하는 어댑터 클래스입니다.
 *
 * @since 2025-05-14
 * @modifed 2025-05-19
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsInfoDetailProviderAdapter implements NewsDetailProviderPort {
	private final NewsInfoJpaRepository newsInfoJpaRepository;
	private final ScrapInfoJpaRepository scrapInfoJpaRepository;

	/**
	 * 주어진 guid(뉴스 ID)를 기준으로 뉴스 상세 정보를 조회합니다.
	 * 조회된 뉴스가 정확히 하나인 경우에만 도메인 객체로 변환하여 반환합니다.
	 *
	 * @param guid 뉴스의 고유 식별자
	 * @return 뉴스 상세 도메인 객체
	 * @author 함예정
	 * @since 2025-05-14
	 * @modified 2025-05-19
	 * 25-05-19 - ScrapInfo를 함께 조회하여 반환하도록 수정
	 */
	@Override
	public Optional<NewsInfoDetail> getNewsInfoDetailsByArticleId(String guid) {
		return newsInfoJpaRepository.findByGuidWithScraps(guid)
			.map(ArticleEntityMapper::toInfoDetailFromData);
	}

	/**
	 * 뉴스 상세 정보를 조회하고, 조회수를 증가시킵니다.
	 *
	 * @param scrapCommand 저장할 Scrap 데이터를 포함하는 커맨드 객체
	 * @since 2025-05-19
	 * @author 정안식
	 */
	@Override
	public void saveScrap(ScrapCommand scrapCommand) {
		scrapInfoJpaRepository.save(toEntity(scrapCommand));
	}
}
