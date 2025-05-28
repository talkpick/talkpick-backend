package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HighlightSegment {
	private int start;
	private int end;
	private long coverCount;
	public HighlightSegment(int start, int end, long coverCount) {
		this.start = start;
		this.end = end;
		this.coverCount = coverCount;
	}
}
