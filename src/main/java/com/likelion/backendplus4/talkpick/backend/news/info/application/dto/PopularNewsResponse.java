package com.likelion.backendplus4.talkpick.backend.news.info.application.dto;

/**
 * 인기뉴스 응답 DTO
 */
public record PopularNewsResponse(
	String guid,           // 객체 식별용
	String title,          // 제목
	String description,    // 본문
	String imageUrl,       // 이미지 링크
	String category        // 카테고리 정보
) {}