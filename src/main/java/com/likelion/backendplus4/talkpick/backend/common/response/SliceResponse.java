package com.likelion.backendplus4.talkpick.backend.common.response;

import java.util.List;

import lombok.Builder;

/**
 * 페이징 처리된 데이터 응답을 나타내는 제네릭 레코드 클래스.
 *
 * @since 2025-05-18
 */
public record SliceResponse<T>(
	List<T> items,
	boolean hasNext) {
}