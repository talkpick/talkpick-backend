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
 * 뉴스 조회수 저장을 담당하는 서비스 클래스입니다.
 * IP 중복 체크 및 비즈니스 정책은 Application Service에서 처리하고,
 * 이 클래스는 순수한 저장 로직만 담당합니다.
 *
 * @since 2025-05-19 최초 작성
 * @modified 2025-06-10 비즈니스 로직 제거, 순수 저장 UseCase로 단순화
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsViewCountService implements NewsViewCountIncreaseUseCase {

    private final NewsViewCountPort newsViewCountPort;
    private final ClientInfoPort clientInfoPort;

    /**
     * 뉴스의 조회수를 저장합니다.
     *
     * Application Service에서 이미 IP 중복 체크 및 비즈니스 정책을 적용했으므로,
     * 이 메서드는 순수한 저장 로직만 수행합니다.
     *
     * @param newsId 뉴스 ID
     * @param viewCount 저장할 조회수
     * @param category 뉴스 카테고리
     * @param publishDate 뉴스 발행일
     * @return 저장된 조회수
     * @author 양병학
     * @since 2025-05-19 최초 작성
     * @modified 2025-06-10 IP 중복 체크 로직 제거, 순수 저장 로직으로 단순화
     */
    @Override
    @Transactional
    public Long increaseViewCount(String newsId, Long viewCount, String category, LocalDateTime publishDate) {
        String ipAddress = clientInfoPort.getClientIpAddress();

        log.debug("조회수 저장 요청 - 뉴스ID: {}, IP: {}, 저장값: {}", newsId, ipAddress, viewCount);

        Long savedViewCount = newsViewCountPort.saveIncreasedViewCount(
            newsId,
            ipAddress,
            category,
            publishDate,
            viewCount
        );

        log.debug("조회수 저장 완료 - 뉴스ID: {}, 저장된 조회수: {}", newsId, savedViewCount);

        return savedViewCount;
    }
}