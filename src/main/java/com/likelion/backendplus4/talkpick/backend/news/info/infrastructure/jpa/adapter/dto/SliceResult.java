package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto;

import java.util.List;

/**
 * 페이징 처리 결과를 담는 제네릭 레코드 클래스.
 *
 * @param <T> 결과 항목의 타입
 * @since 2025-05-18
 */
public record SliceResult<T>(
	List<T> content,
	boolean hasNext
) {}