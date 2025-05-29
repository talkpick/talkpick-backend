package com.likelion.backendplus4.talkpick.backend.news.info.presentation.mapper;

import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {

	public String toKoreanCategory(String englishCategory) {
		return switch (englishCategory.toLowerCase()) {
			case "all" -> "전체";
			case "politics" -> "정치";
			case "society" -> "사회";
			case "economy" -> "경제";
			case "sports" -> "스포츠";
			case "international" -> "국제";
			case "entertainment" -> "연예";
			default -> throw new IllegalArgumentException("Invalid category: " + englishCategory);
		};
	}
}
