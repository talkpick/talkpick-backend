package com.likelion.backendplus4.talkpick.backend.news.info.application.port.out;

import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;

/**
 * 인기뉴스 랭킹 조회를 위한 포트 인터페이스입니다.
 * 
 * 뉴스 랭킹 데이터 조회, 해시값 관리, 캐시 관리 등의 기능을 정의합니다.
 *
 * @author 양병학
 * @since 2025-05-27 최초 작성
 */
public interface PopularNewsPort {

    /**
     * 특정 카테고리의 Top1 뉴스 ID를 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return Top1 뉴스 ID, 없으면 null
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    String getTop1NewsId(String category);

    /**
     * 특정 카테고리의 Top1 뉴스와 점수를 조회합니다.
     * 
     * 해시 계산에 사용되는 데이터를 반환합니다.
     *
     * @param category 조회할 카테고리명
     * @return "뉴스ID:점수" 형태의 문자열, 없으면 "empty"
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    String getTop1NewsWithScore(String category);

    /**
     * 랭킹 변화 감지를 위한 해시값을 저장합니다.
     *
     * @param category  카테고리명
     * @param hashValue 저장할 해시값
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    void saveRankingHash(String category, String hashValue);

    /**
     * 저장된 랭킹 해시값을 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return 저장된 해시값
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    String getSavedRankingHash(String category);

    /**
     * 카테고리별 Top1 뉴스 결과를 2차 캐시에 저장합니다.
     *
     * @param category 카테고리명
     * @param topNews  저장할 뉴스 데이터
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    void saveTopNews(String category, PopularNewsResponse topNews);

    /**
     * 카테고리별 Top1 뉴스 결과를 2차 캐시에서 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return 캐시된 뉴스 데이터, 없으면 null
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    PopularNewsResponse getTopNews(String category);

}