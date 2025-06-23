package com.likelion.backendplus4.talkpick.backend.news.info.domain.model;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * 뉴스 메타데이터만을 표현하는 경량 도메인 모델 클래스입니다.
 * 조회수 증가 등의 작업에서 사용됩니다.
 *
 * @since 2025-06-10
 */
@Builder
@Getter
public class NewsInfoMetadata {
	private String newsId;
	private String category;
	private LocalDateTime pubDate;
}
