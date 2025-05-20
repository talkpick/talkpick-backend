package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.mapper;

import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.item.ViewCountItem;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.entity.ArticleEntity;

/**
 * ViewCountItem과 ArticleEntity 간의 변환을 담당하는 유틸리티 클래스입니다.
 *
 * @since 2025-05-23
 */
public class ViewCountMapper {

	/**
	 * ViewCountItem을 업데이트용 ArticleEntity로 변환합니다.
	 * ID와 guid만 포함된 엔티티를 생성합니다.
	 *
	 * @param item ViewCountItem
	 * @param viewCount 설정할 조회수
	 * @return 변환된 ArticleEntity
	 */
	public static ArticleEntity toEntity(ViewCountItem item, Long viewCount) {
		ArticleEntity entity = new ArticleEntity();
		entity.setGuid(item.getNewsId());
		entity.setViewCount(viewCount);
		return entity;
	}
}