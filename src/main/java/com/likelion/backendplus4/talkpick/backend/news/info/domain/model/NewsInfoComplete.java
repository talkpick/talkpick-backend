package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class NewsInfoComplete {
	private final String newsId;
	private final String title;
	private final String originLink;
	private final LocalDateTime publishDate;
	private final String category;
	private final String content;
	private final String imageUrl;
	private final String summary;
	private final List<HighlightSegment> highlights;
}