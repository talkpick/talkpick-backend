package com.likelion.backendplus4.talkpick.backend.news.info.application.port.out;

import java.util.List;
import java.util.Optional;

import com.likelion.backendplus4.talkpick.backend.news.info.application.command.ScrapCommand;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoMetadata;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.ScrapInfo;

/**
 * 뉴스의 상세 정보를 인프라 계층에서 조회하기 위한 포트 인터페이스입니다.
 *
 * @since 2025-05-14
 */
	public interface NewsDetailProviderPort {

	/**
	 * 주어진 guid(뉴스 ID)를 기반으로 뉴스 상세 정보를 조회합니다.
	 *
	 * @param guid 뉴스 고유 식별자
	 * @return 뉴스 상세 도메인 객체
	 * @author 함예정
	 * @since 2025-05-14
	 */
	Optional<NewsInfoDetail> getNewsInfoDetailsByArticleId(String guid);

	Optional<NewsInfoMetadata> getNewsInfoMetadataByArticleId(String guid);

	List<NewsInfoDetail> getNewsInfoDetailsByUserId(Long userId);
	void saveScrap(ScrapCommand scrapCommand);
}
