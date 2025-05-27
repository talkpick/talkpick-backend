package com.likelion.backendplus4.talkpick.backend.news.info.application.dto;

/**
 * 인기뉴스 응답 데이터를 담는 DTO입니다.
 *
 * 클라이언트에게 반환되는 인기뉴스 정보를 정의합니다.
 * Record 타입을 사용하여 불변성을 보장합니다.
 *
 * @param guid        뉴스 고유 식별자
 * @param title       뉴스 제목
 * @param description 뉴스 본문 요약
 * @param imageUrl    뉴스 대표 이미지 URL
 * @param category    뉴스 카테고리
 * @author 개발자
 * @since 2025-05-27 최초 작성
 */
public record PopularNewsResponse(
        String guid,
        String title,
        String description,
        String imageUrl,
        String category
) {
}