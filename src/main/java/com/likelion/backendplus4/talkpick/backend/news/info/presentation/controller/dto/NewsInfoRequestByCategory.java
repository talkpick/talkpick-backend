package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto;
/**
 * 카테고리별 뉴스 조회 요청 DTO
 *
 * @since 2025-05-26
 */
public record NewsInfoRequestByCategory(
	String lastId,
	Integer size) {

	public NewsInfoRequestByCategory {
		if (size == null) {
			size = 10;
		}
	}
}
