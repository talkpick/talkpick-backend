package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "scrap")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@EqualsAndHashCode(of = "id")
public class ScrapEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "news_id", nullable = false, length = 100)
	private String newsId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "paragraph_index", nullable = false)
	private int paragraphIndex;

	@Column(name = "snippet_text", nullable = false, columnDefinition = "TEXT")
	private String snippetText;

	@Column(name = "start_offset", nullable = false)
	private int startOffset;

	@Column(name = "end_offset", nullable = false)
	private int endOffset;

	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}
