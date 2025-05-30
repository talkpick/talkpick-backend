package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request;
/**
 * 뉴스 조회 요청 DTO
 *
 * @since 2025-05-26
 */
public record NewsInfoRequest(
	String lastId,
	Integer size) {

	public NewsInfoRequest {
		if (size == null) {
			size = 10;
		}
	}
}
