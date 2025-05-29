package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.mapper;

import java.time.LocalDateTime;

import com.likelion.backendplus4.talkpick.backend.news.info.application.command.ScrapCommand;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.ScrapInfo;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ScrapEntity;

public class ScrapEntityMapper {

	private ScrapEntityMapper() {}

	public static ScrapEntity toEntity(ScrapCommand cmd) {
		return ScrapEntity.builder()
			.newsId(cmd.newsId())
			.paragraphIndex(cmd.paragraphIndex())
			.snippetText(cmd.snippetText())
			.startOffset(cmd.startOffset())
			.endOffset(cmd.endOffset())
			.build();
	}

	public static ScrapInfo toDomain(ScrapEntity e) {
		return new ScrapInfo(
			e.getId(),
			e.getNewsId(),
			e.getParagraphIndex(),
			e.getSnippetText(),
			e.getStartOffset(),
			e.getEndOffset(),
			e.getCreatedAt()
		);
	}
}