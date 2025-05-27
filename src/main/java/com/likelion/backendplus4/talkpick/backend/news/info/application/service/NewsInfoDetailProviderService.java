package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * NewsInfoProviderUseCase를 구현하는 서비스 클래스입니다.
 * 뉴스 상세 정보 조회 및 조회수를 함께 제공합니다.
 *
 * @modified 2025-05-27
 * @since 2025-05-14
 */
@Service
@RequiredArgsConstructor
public class NewsInfoDetailProviderService implements NewsInfoDetailProviderUseCase {
    private final NewsDetailProviderPort newsDetailProviderPort;
    private final NewsViewCountIncreaseUseCase newsViewCountIncreaseUseCase;

    /**
     * 뉴스 ID를 기반으로 뉴스 상세 정보와 현재 조회수를 함께 조회합니다.
     *
     * 1. 현재 조회수 조회
     * 2. 어댑터를 통해 뉴스 상세 정보와 조회수가 결합된 응답 반환
     *
     * @param newsId 조회할 뉴스의 ID
     * @return 뉴스 상세 정보와 조회수가 포함된 응답 객체
     * @author 양병학
     * @modify 2025-05-27 양병학
     * - Mapper 로직을 어댑터로 이관
     * @since 2025-05-19 최초 작성
     */
    @Override
    public NewsInfoDetailResponse getNewsInfoDetailByNewsId(String newsId) {
        Long currentViewCount = fetchCurrentViewCount(newsId);
        return newsDetailProviderPort.getNewsInfoDetailResponseWithViewCount(newsId, currentViewCount);
    }

    /**
     * 뉴스의 현재 조회수를 조회합니다.
     *
     * @param newsId 조회할 뉴스의 ID
     * @return 현재 조회수
     */
    private Long fetchCurrentViewCount(String newsId) {
        return newsViewCountIncreaseUseCase.getCurrentViewCount(newsId);
    }
}