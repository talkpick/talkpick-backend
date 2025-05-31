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
     *
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
        log.info("조회수 증가 요청 - 뉴스ID: {}, IP: {}", newsId, ipAddress);

        return processViewCountWithDuplicateCheck(newsId, ipAddress, category, publishDate);
    }

    /**
     * 중복 체크를 포함한 조회수 증가 처리를 수행합니다.
     *
     * @param newsId 뉴스 ID
     * @param ipAddress 클라이언트 IP 주소
     * @param category 뉴스 카테고리
     * @param publishDate 뉴스 발행일
     * @return 처리된 조회수
     * @author 양병학
     * @since 2025-06-01 최초 작성
     */
    private Long processViewCountWithDuplicateCheck(String newsId, String ipAddress, String category, LocalDateTime publishDate) {
        boolean hasHistory = checkViewHistory(newsId, ipAddress);

        if (!hasHistory) {
            return increaseViewCountInternal(newsId, ipAddress, category, publishDate);
        } else {
            return getCurrentViewCountWhenDuplicate(newsId, ipAddress);
        }
    }

    /**
     * 조회 이력을 확인합니다.
     *
     * @param newsId 뉴스 ID
     * @param ipAddress 클라이언트 IP 주소
     * @return 조회 이력 존재 여부
     * @author 양병학
     * @since 2025-06-01 최초 작성
     */
    private boolean checkViewHistory(String newsId, String ipAddress) {
        boolean hasHistory = newsViewCountPort.hasViewHistory(newsId, ipAddress);
        log.info("조회 이력 확인 - 뉴스ID: {}, IP: {}, 이력있음: {}", newsId, ipAddress, hasHistory);
        return hasHistory;
    }

    /**
     * 실제 조회수 증가를 수행합니다.
     *
     * @param newsId 뉴스 ID
     * @param ipAddress 클라이언트 IP 주소
     * @param category 뉴스 카테고리
     * @param publishDate 뉴스 발행일
     * @return 증가된 조회수
     * @author 양병학
     * @since 2025-06-01 최초 작성
     */
    private Long increaseViewCountInternal(String newsId, String ipAddress, String category, LocalDateTime publishDate) {
        log.info("조회수 증가 실행 - 뉴스ID: {}", newsId);
        Long newViewCount = newsViewCountPort.increaseViewCount(newsId, ipAddress, category, publishDate);
        log.info("조회수 증가 처리 완료 - 뉴스ID: {}, 새 조회수: {}", newsId, newViewCount);
        return newViewCount;
    }

    /**
     * 중복 조회일 때 현재 조회수를 반환합니다.
     *
     * @param newsId 뉴스 ID
     * @param ipAddress 클라이언트 IP 주소
     * @return 현재 조회수
     * @author 양병학
     * @since 2025-06-01 최초 작성
     */
    private Long getCurrentViewCountWhenDuplicate(String newsId, String ipAddress) {
        log.info("이미 조회한 사용자 - 조회수 증가 안 함 - 뉴스ID: {}, IP: {}", newsId, ipAddress);
        return newsViewCountPort.getCurrentViewCount(newsId);
    }
}