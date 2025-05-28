package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.ScrapInfo;

@Component
public class HighlightCalculator {

	/**
	 * ScrapInfo 리스트(startOffset, endOffset)를 받아
	 * 하이라이트 세그먼트를 계산해 반환합니다.
	 */
	public List<HighlightSegment> computeSegments(List<ScrapInfo> scraps) {
		// 기존 Event 생성부만, HighlightSegment 대신 ScrapInfo 사용
		List<Event> events = new ArrayList<>();
		for (ScrapInfo s : scraps) {
			events.add(new Event(s.getStartOffset(), +1));
			events.add(new Event(s.getEndOffset(),   -1));
		}
		events.sort(Comparator.comparingInt(e -> e.offset));

		List<HighlightSegment> segments = new ArrayList<>();
		int count = 0;
		int prev = events.isEmpty() ? 0 : events.get(0).offset;
		for (Event e : events) {
			int curr = e.offset;
			if (0 < count && curr > prev) {
				segments.add(new HighlightSegment(prev, curr, count));
			}
			count += e.delta;
			prev = curr;
		}
		return segments;
	}

	private record Event(int offset, int delta) {}
}
