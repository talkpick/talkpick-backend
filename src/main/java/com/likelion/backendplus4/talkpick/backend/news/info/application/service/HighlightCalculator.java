package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.ScrapInfo;

@Component
public class HighlightCalculator {

	public List<HighlightSegment> computeSegments(List<ScrapInfo> scraps) {
		return scraps.stream()
			.collect(Collectors.groupingBy(ScrapInfo::getParagraphIndex))
			.entrySet().stream()
			.flatMap(entry -> computeSegmentsForParagraph(entry.getKey(), entry.getValue()).stream())
			.toList();
	}

	private List<HighlightSegment> computeSegmentsForParagraph(int paragraphIndex, List<ScrapInfo> scraps) {
		// 이벤트 생성 및 정렬
		List<Event> events = scraps.stream()
			.flatMap(s -> Stream.of(
				new Event(s.getStartOffset(),  1),
				new Event(s.getEndOffset(),   -1)
			))
			.sorted(Comparator.comparingInt(Event::offset))
			.toList();

		List<HighlightSegment> segments = new ArrayList<>();
		int count = 0;
		int prevOffset = events.isEmpty() ? 0 : events.get(0).offset();

		for (Event event : events) {
			int currOffset = event.offset();
			if (currOffset > prevOffset && count > 0) {
				segments.add(new HighlightSegment(
					paragraphIndex,
					prevOffset,
					currOffset,
					count
				));
			}
			count += event.delta();
			prevOffset = currOffset;
		}
		return segments;
	}

	private record Event(int offset, int delta) {}
}
