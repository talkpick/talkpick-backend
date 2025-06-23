package com.likelion.backendplus4.talkpick.backend.news.info.application.port.out;

import java.time.LocalDateTime;

public interface NewsViewCountPort {
    /**
     * 뉴스의 증가된 조회수를 저장합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 증가 후 조회수
     */
    Long saveIncreasedViewCount(String newsId, String ipAddress, String category, LocalDateTime publishDate, Long viewCount);

    /**
     * 특정 IP의 뉴스 조회 기록을 저장합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 저장 성공 여부
     */
    boolean saveViewHistory(String newsId, String ipAddress);

    /**
     * 특정 IP의 뉴스 조회 기록이 있는지 확인합니다.
     *
     * @param newsId    뉴스 ID
     * @param ipAddress 사용자 IP 주소
     * @return 조회 기록 존재 여부
     */
    boolean hasViewHistory(String newsId, String ipAddress);

    /**
     * 뉴스의 현재 조회수를 조회합니다.
     * Redis에 있으면 Redis에서, 없으면 DB에서 조회합니다.
     *
     * @param newsId 뉴스 ID
     * @return 현재 조회수
     */
    Long getCurrentViewCount(String newsId);
}