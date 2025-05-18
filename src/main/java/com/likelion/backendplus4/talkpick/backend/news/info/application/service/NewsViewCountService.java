package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.application.port.out.NewsViewCountPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 뉴스 조회수 관련 서비스를 제공하는 클래스입니다.
 *
 * @since 2025-05-19 최초 작성
 */
@Service
@RequiredArgsConstructor
public class NewsViewCountService implements NewsViewCountIncreaseUseCase {

	private final NewsViewCountPort newsViewCountPort;

	/**
	 * 뉴스의 조회수를 증가시키는 메서드입니다.
	 *
	 * 1. IP 주소 기반으로 해당 뉴스에 대한 조회 이력 확인
	 * 2. 조회 이력이 없는 경우에만 조회수 증가
	 * 3. 조회 이력 저장
	 *
	 * @param newsId 조회수를 증가시킬 뉴스의 ID
	 * @param ipAddress 사용자의 IP 주소
	 * @since 2025-05-19 최초 작성
	 * @author 양병학
	 *
	 */
	@Override
	@Transactional
	public void increaseViewCount(String newsId, String ipAddress) {
		if (!newsViewCountPort.hasViewHistory(newsId, ipAddress)) {
			newsViewCountPort.increaseViewCount(newsId);
			newsViewCountPort.saveViewHistory(newsId, ipAddress);
		}
	}
}