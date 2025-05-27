package com.likelion.backendplus4.talkpick.backend.news.info.application.port.out;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.NewsInfoDetailResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoDetail;

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
    NewsInfoDetail getNewsInfoDetailsByArticleId(String guid);

    /**
     * 주어진 guid(뉴스 ID)를 기반으로 인기뉴스 응답 형태로 조회합니다.
     *
     * 인기뉴스 조회에 최적화된 데이터 형태로 반환하며,
     * 변환 로직은 어댑터에서 담당합니다.
     *
     * @param guid 뉴스 고유 식별자
     * @return 인기뉴스 응답 DTO
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    PopularNewsResponse getPopularNewsByArticleId(String guid);

    /**
     * 뉴스 ID와 조회수를 함께 받아 NewsInfoDetailResponse로 반환합니다.
     *
     * 뉴스 상세 조회에 최적화된 형태로, 현재 조회수가 반영된 응답을 제공합니다.
     * 변환 로직은 어댑터에서 담당합니다.
     *
     * @param newsId    뉴스 고유 식별자
     * @param viewCount 현재 조회수
     * @return 뉴스 상세 응답 DTO (조회수 포함)
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    NewsInfoDetailResponse getNewsInfoDetailResponseWithViewCount(String newsId, Long viewCount);
}
