package com.likelion.backendplus4.talkpick.backend.news.info.presentation.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.application.command.ScrapCommand;
import com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller.dto.request.ScrapRequest;

public class ScrapCommandMapper {

	private ScrapCommandMapper() {}

	public static ScrapCommand toCommand(String newsId, Long loginUser,ScrapRequest req) {
		return new ScrapCommand(

			newsId,
			loginUser,
			req.getParagraphIndex(),
			req.getSnippetText(),
			req.getStartOffset(),
			req.getEndOffset()
		);
	}
}