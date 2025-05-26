package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto;

public record NewsInfoRequestByCategory(
	String lastId,
	Integer size,
	String category) {

	public NewsInfoRequestByCategory {
		if (size == null) {
			size = 10;
		}
	}
}
