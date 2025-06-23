package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoCompleteMapper.*;
import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoViewCountMapper.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogMethodValues;
import com.likelion.backendplus4.talkpick.backend.news.info.application.command.ScrapCommand;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.ClientInfoPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsViewCountPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.support.HighlightCalculator;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.HighlightSegment;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoMetadata;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoViewCount;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * NewsInfoProviderUseCase를 구현하는 서비스 클래스입니다.
 * 뉴스 상세 정보 조회 및 조회수를 함께 제공합니다.
 *
 * @modified 2025-05-19
 * @since 2025-05-14
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsInfoDetailProviderService implements NewsInfoDetailProviderUseCase {
    private final NewsDetailProviderPort newsDetailProviderPort;
    private final NewsViewCountPort newsViewCountPort;
    private final NewsViewCountIncreaseUseCase newsViewCountIncreaseUseCase;
    private final HighlightCalculator highlightCalculator;
    private final ClientInfoPort clientInfoPort;

    private final Semaphore semaphore = new Semaphore(5);
    private final ConcurrentHashMap<String, ReentrantLock> newsLocks = new ConcurrentHashMap<>();

    /**
     * 뉴스 ID를 기반으로 뉴스 상세 정보 조회
     *
     * @param newsId 조회할 뉴스의 ID
     * @return 뉴스 상세 정보가 포함된 응답 객체
     * @author 양병학
     * @since 2025-05-19
     */
    @EntryExitLog
    @LogMethodValues
    @Override
    public NewsInfoComplete getNewsInfoDetailByNewsId(String newsId) {
        NewsInfoDetail detail = fetchNewsInfoDetail(newsId);

        List<HighlightSegment> highlightSegments = highlightCalculator.computeSegments(detail.getScrapInfos());

        return toNewsInfoComplete(detail, highlightSegments);
    }

    /**
     * 뉴스 ID를 기반으로 조회수를 증가시키는 로직 실행 (일반 조회수 redis + 인기 뉴스용 조회수 redis)
     * IP 중복 체크 및 비즈니스 정책을 관리합니다.
     *
     * @param newsId 조회할 뉴스의 ID
     * @return 뉴스 조회수 응답객체
     * @author 양병학
     * @since 2025-06-08
     * @modified 2025-06-10 IP 중복 체크 로직을 현재위치로 이동
     */
    public NewsInfoViewCount getNewsInfoViewCount(String newsId) {
        try {
            semaphore.acquire();

            NewsInfoMetadata metadata = fetchNewsInfoDetailWithCache(newsId);
            String clientIp = getClientIpAddress();
            ReentrantLock lock = newsLocks.computeIfAbsent(newsId, k -> new ReentrantLock());

            lock.lock();
            try {
                NewsInfoViewCount domain = createInitialDomain(newsId, metadata);
                if (shouldIncreaseViewCount(domain, newsId, clientIp)) {
                    domain.addViewCount();
                    saveIncreasedViewCount(newsId, domain.getViewCount(), metadata);
                    return domain;
                }
                return domain;
            } finally {
                lock.unlock();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("처리 중단됨");
        } finally {
            semaphore.release();
        }
    }

    /**
     * 클라이언트 IP 주소를 조회합니다.
     *
     * @author 양병학
     * @return 클라이언트 IP 주소
     */
    private String getClientIpAddress() {
        return clientInfoPort.getClientIpAddress();
    }

    /**
     * 초기 도메인 객체를 생성합니다.
     *
     * @param newsId 뉴스 ID
     * @param metadata 뉴스 메타데이터
     * @author 양병학
     * @return 초기 도메인 객체
     */
    private NewsInfoViewCount createInitialDomain(String newsId, NewsInfoMetadata metadata) {
        Long currentViewCount = newsViewCountPort.getCurrentViewCount(newsId);
        return buildDomain(newsId, currentViewCount, metadata.getCategory(), metadata.getPubDate());
    }

    /**
     * 조회수 증가 처리를 수행합니다.
     *
     * @param domain 도메인 객체
     * @param newsId 뉴스 ID
     * @param metadata 뉴스 메타데이터
     * @author 양병학
     * @return 조회수가 증가된 도메인 객체
     */
    private NewsInfoViewCount processViewCountIncrease(NewsInfoViewCount domain, String newsId, NewsInfoMetadata metadata) {
        domain.addViewCount();

        saveIncreasedViewCount(newsId, domain.getViewCount(), metadata);

        return domain;
    }

    /**
     * 증가된 조회수를 저장합니다.
     *
     * @param newsId 뉴스 ID
     * @param viewCount 증가된 조회수
     * @param metadata 뉴스 메타데이터
     * @author 양병학
     */
    private void saveIncreasedViewCount(String newsId, Long viewCount, NewsInfoMetadata metadata) {
        newsViewCountIncreaseUseCase.increaseViewCount(
            newsId,
            viewCount,
            metadata.getCategory(),
            metadata.getPubDate()
        );
    }

    /**
     * 조회수 증가 가능 여부를 판단합니다.
     * 도메인의 랭킹 적용 가능 여부와 IP 중복 체크를 조율합니다.
     *
     * @param domain 뉴스 조회수 도메인 객체
     * @param newsId 뉴스 ID
     * @param clientIp 클라이언트 IP
     * @return 조회수 증가 가능 여부
     * @author 양병학
     * @since 2025-06-10
     */
    private boolean shouldIncreaseViewCount(NewsInfoViewCount domain, String newsId, String clientIp) {
        if (!domain.isEligibleForRanking()) {
            log.debug("랭킹 대상이 아님 - 뉴스ID: {}", newsId);
            return false;
        }

        // if (newsViewCountPort.hasViewHistory(newsId, clientIp)) {
        //     log.debug("IP 중복 조회 - 뉴스ID: {}, IP: {}", newsId, clientIp);
        //     return false;
        // }

        log.debug("조회수 증가 가능 - 뉴스ID: {}, IP: {}", newsId, clientIp);
        return true;
    }

    private NewsInfoViewCount buildDomain(String newsId, Long viewCount, String category, LocalDateTime publishDate) {
        return toNewsInfoViewCount(newsId, viewCount, publishDate, category);
    }

    /**
     * 뉴스 메타데이터를 캐시와 함께 조회합니다.
     *
     * @param newsId 뉴스 ID
     * @return 뉴스 메타데이터
     * @author 양병학
     * @since 2025-06-10
     */
    @Cacheable(value = "newsMetadata", key = "#newsId")
    private NewsInfoMetadata fetchNewsInfoDetailWithCache(String newsId) {
        return newsDetailProviderPort.getNewsInfoMetadataByArticleId(newsId)
            .orElseThrow(() -> new NewsInfoException(NewsInfoErrorCode.NEWS_NOT_FOUND));
    }

    /**
     * 사용자 ID를 기반으로 뉴스 상세 정보 목록을 조회합니다.
     * 각 뉴스의 스크랩 정보를 포함하여 하이라이트 세그먼트를 계산합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 사용자와 관련된 뉴스 상세 정보 목록
     * @author 정안식
     * @since 2025-05-30
     */
    @EntryExitLog
    @Override
    public List<NewsInfoComplete> getNewsInfoDetailByUserId(Long userId) {

        List<NewsInfoDetail> details = fetchNewsInfoDetailWithUserId(userId);

        return details.stream()
                .map(detail ->
                        combineNewsInfoByUserId(
                                detail,
                                highlightCalculator.computeSegments(detail.getScrapInfos())
                        )
                )
                .toList();
    }

    /**
     * 뉴스 ID를 기반으로 뉴스 상세 정보를 조회합니다.
     *
     * @param newsId 조회할 뉴스의 ID
     * @return 뉴스 상세 도메인 객체
     * @author 이해창
     * @since 2025-06-04
     */
    @Override
    public NewsInfoDetail getNewsDetail(String newsId) {
        return fetchNewsInfoDetail(newsId);
    }

    @Override
    public void saveScrap(ScrapCommand scrapCommand) {
        newsDetailProviderPort.saveScrap(scrapCommand);
    }

    /**
     * 뉴스 상세 정보를 조회합니다.
     *
     * @param newsId 조회할 뉴스의 ID
     * @return 뉴스 상세 도메인 객체
     * @author 정안식
     * @since 2025-05-30
     */
    private NewsInfoDetail fetchNewsInfoDetail(String newsId) {
        return newsDetailProviderPort
                .getNewsInfoDetailsByArticleId(newsId)
                .orElseThrow(() -> new NewsInfoException(NewsInfoErrorCode.NEWS_NOT_FOUND));
    }

    /**
     * 사용자 ID를 기반으로 뉴스 상세 정보 목록을 조회합니다.
     *
     * @param userId 조회할 사용자의 ID
     * @return 뉴스 상세 정보 목록
     * @author 정안식
     * @since 2025-05-30
     */
    private List<NewsInfoDetail> fetchNewsInfoDetailWithUserId(Long userId) {
        return newsDetailProviderPort
                .getNewsInfoDetailsByUserId(userId);
    }

    /**
     * 뉴스 상세 정보와 하이라이트 세그먼트를 결합하여 완전한 뉴스 정보를 생성합니다.
     *
     * @param newsInfoDetail 뉴스 상세 정보
     * @param highlightSegments 하이라이트 세그먼트 목록
     * @return 사용자별 완전한 뉴스 정보 객체
     * @author 정안식
     * @since 2025-05-30
     */
    private NewsInfoComplete combineNewsInfoByUserId(NewsInfoDetail newsInfoDetail, List<HighlightSegment> highlightSegments) {
        return toNewsInfoCompleteByUserId(newsInfoDetail, highlightSegments);
    }
}