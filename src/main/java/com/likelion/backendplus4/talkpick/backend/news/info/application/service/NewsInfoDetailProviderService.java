package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoCompleteMapper.*;
import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoViewCountMapper.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogMethodValues;
import com.likelion.backendplus4.talkpick.backend.news.info.application.command.ScrapCommand;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsViewCountPort;
import com.likelion.backendplus4.talkpick.backend.news.info.application.support.HighlightCalculator;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.HighlightSegment;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoViewCount;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * NewsInfoProviderUseCase를 구현하는 서비스 클래스입니다.
 * 뉴스 상세 정보 조회 및 조회수를 함께 제공합니다.
 *
 * @modified 2025-05-19
 * @since 2025-05-14
 */
@Service
@RequiredArgsConstructor
public class NewsInfoDetailProviderService implements NewsInfoDetailProviderUseCase {
    private final NewsDetailProviderPort newsDetailProviderPort;
    private final NewsViewCountPort newsViewCountPort;
    private final NewsViewCountIncreaseUseCase newsViewCountIncreaseUseCase;
    private final HighlightCalculator highlightCalculator;

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
     * 증가 이후 조회수 데이터를 매핑해 반환
     *
     * @param newsId      조회할 뉴스의 ID
     * @return 뉴스 조회수 응답객체
     * @author 양병학
     * @since 2025-06-08
     */
    public NewsInfoViewCount getNewsInfoViewCount(String newsId) {

        NewsInfoDetail newsDetail = fetchNewsInfoDetailWithCache(newsId);
        Long currentViewCount = newsViewCountPort.getCurrentViewCount(newsId);

        NewsInfoViewCount domain = buildDomain(newsId, currentViewCount, newsDetail.getCategory(), newsDetail.getPubDate());

        if (domain.isEligibleForRanking()) {
            domain.addViewCount();

            newsViewCountIncreaseUseCase.increaseViewCount(newsId, domain.getViewCount(), newsDetail.getCategory(), newsDetail.getPubDate());
        }

        return domain;
    }

    private NewsInfoViewCount buildDomain(String newsId, Long viewCount ,String category, LocalDateTime publishDate){
        NewsInfoViewCount domain = toNewsInfoViewCount(
            newsId,
            viewCount,
            publishDate,
            category
        );

        return domain;
    }


    @Cacheable(value = "newsMetadata", key = "#newsId")
    private NewsInfoDetail fetchNewsInfoDetailWithCache(String newsId) {
        return fetchNewsInfoDetail(newsId);
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