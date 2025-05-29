package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ScrapInfo {
	private final Long id;
	private final String newsId;
	private final int paragraphIndex;
	private final String snippetText;
	private final int startOffset;
	private final int endOffset;
	private final LocalDateTime createdAt;
}
