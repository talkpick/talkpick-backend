package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import java.time.LocalDateTime;
import java.util.List;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.LogMethodValues;
import com.likelion.backendplus4.talkpick.backend.news.info.application.command.ScrapCommand;

import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoCompleteMapper.*;
import static com.likelion.backendplus4.talkpick.backend.news.info.application.mapper.NewsInfoViewCountMapper.*;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsViewCountIncreaseUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.support.HighlightCalculator;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.HighlightSegment;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.NewsDetailProviderPort;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoViewCount;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
     * @param category    인기뉴스 조회수 redis 증가용 카테고리
     * @param publishDate 인기뉴스 조회수 redis 최신 뉴스 분별용 발행일자
     * @return 뉴스 조회수 응답객체
     * @author 양병학
     * @since 2025-06-08
     */
    public NewsInfoViewCount getNewsInfoViewCount(String newsId, String category, LocalDateTime publishDate) {
        Long viewCount = newsViewCountIncreaseUseCase.increaseViewCount(newsId, category, publishDate);
        return toNewsInfoViewCount(newsId, viewCount);
    }

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
     */
    private NewsInfoDetail fetchNewsInfoDetail(String newsId) {
        return newsDetailProviderPort
                .getNewsInfoDetailsByArticleId(newsId)
                .orElseThrow(() -> new NewsInfoException(NewsInfoErrorCode.NEWS_NOT_FOUND));
    }

    private List<NewsInfoDetail> fetchNewsInfoDetailWithUserId(Long userId) {
        return newsDetailProviderPort
                .getNewsInfoDetailsByUserId(userId);
    }

    private NewsInfoComplete combineNewsInfoByUserId(NewsInfoDetail newsInfoDetail, List<HighlightSegment> highlightSegments) {
        return toNewsInfoCompleteByUserId(newsInfoDetail, highlightSegments);
    }
}