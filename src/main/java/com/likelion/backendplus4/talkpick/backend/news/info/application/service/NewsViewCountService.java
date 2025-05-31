package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import java.time.LocalDateTime;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.ClientInfoPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsViewCountPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 뉴스 조회수 관련 서비스를 제공하는 클래스입니다.
 *
 * @since 2025-05-19 최초 작성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsViewCountService implements NewsViewCountIncreaseUseCase {

    private final NewsViewCountPort newsViewCountPort;
    private final ClientInfoPort clientInfoPort;

    /**
     * 뉴스의 조회수를 증가시키는 메서드입니다.
     * <p>
     * 1. IP 주소 기반으로 해당 뉴스에 대한 조회 이력 확인
     * 2. 조회 이력이 없는 경우에만 조회수 증가
     * 3. 조회 이력 저장
     *
     * @param newsId    조회수를 증가시킬 뉴스의 ID
     * @author 양병학
     * @since 2025-05-19 최초 작성
     */
    @Override
    @Transactional
    public Long increaseViewCount(String newsId, String category, LocalDateTime publishDate) {
        String ipAddress = clientInfoPort.getClientIpAddress();

        log.debug("조회수 증가 요청 - 뉴스ID: {}, IP: {}", newsId, ipAddress);

        Long newViewCount = newsViewCountPort.increaseViewCount(newsId, ipAddress, category, publishDate);

        log.debug("조회수 증가 처리 완료 - 뉴스ID: {}", newsId);

        return newViewCount;
    }
}