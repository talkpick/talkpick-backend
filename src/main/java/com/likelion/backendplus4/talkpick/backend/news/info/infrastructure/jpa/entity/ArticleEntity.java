package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.converter.FloatArrayToJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Converter;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * RSS 피드에서 수집한 뉴스 기사를 저장하는 JPA 엔티티 클래스입니다.
 * 각 기사 데이터는 데이터베이스의 "article" 테이블에 매핑됩니다.
 *
 * @author 양병학
 * @since 2025-05-10 최초 작성
 * @modified 2025-05-10
 */
@Entity
@Table(name = "article", uniqueConstraints = @UniqueConstraint(columnNames = {"link"}))
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "description")
@EqualsAndHashCode(of = "id")
public class ArticleEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;

	@Column(nullable = false, unique = true)
	private String link;

	@Column(name = "pub_date")
	private LocalDateTime pubDate;

	@Column
	private String category;

	@Column
	@Setter
	private String guid;

	@Column(columnDefinition = "TEXT")
	private String description;

	@Column(name = "image_url")
	private String imageUrl;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "summary", columnDefinition = "TEXT")
	private String summary;

	@Convert(converter = FloatArrayToJsonConverter.class)
	@Column(name = "summary_vector", columnDefinition = "JSON")
	private float[] summaryVector;

  @Setter
  @Column(name = "view_count", nullable = false)
  private Long viewCount;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	public String getDescription() {
		return description != null ? description : "";
	}
}