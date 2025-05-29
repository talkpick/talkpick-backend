package com.likelion.backendplus4.talkpick.backend.news.info.application.command;

public record ScrapCommand(
	String newsId,
	int paragraphIndex,
	String snippetText,
	int startOffset,
	int endOffset
) {}
