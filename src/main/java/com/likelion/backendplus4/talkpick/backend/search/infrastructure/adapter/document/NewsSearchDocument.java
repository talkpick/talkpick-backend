package com.likelion.backendplus4.talkpick.backend.search.infrastructure.adapter.document;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import lombok.Getter;
import lombok.Setter;

/**
 * Elasticsearch news_index 인덱스에 매핑되는 문서 모델 클래스
 *
 * @since 2025-05-15
 */
@Document(indexName = "news_index")
@Getter
@Setter
public class NewsSearchDocument {
	@Id
	private String newsId;
	private String title;
	private String content;
	@Field(type = FieldType.Date, format = { DateFormat.epoch_millis })
	private Instant publishedAt;
	private String imageUrl;
	private String category;
}
