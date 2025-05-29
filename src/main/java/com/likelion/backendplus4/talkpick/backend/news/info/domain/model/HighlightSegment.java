package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class HighlightSegment {
	private int paragraphIndex;
	private int start;
	private int end;
	private long coverCount;

	public HighlightSegment(int paragraphIndex,int start, int end, long coverCount) {
		this.paragraphIndex = paragraphIndex;
		this.start = start;
		this.end = end;
		this.coverCount = coverCount;
	}
}
