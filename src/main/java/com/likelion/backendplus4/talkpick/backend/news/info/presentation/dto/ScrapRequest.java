package com.likelion.backendplus4.talkpick.backend.news.info.presentation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Value;

@Value
public class ScrapRequest {

	@PositiveOrZero(message = "paragraphIndex는 필수입니다.")
	int paragraphIndex;

	@NotBlank(message = "snippetText는 필수입니다.")
	String snippetText;

	@PositiveOrZero(message = "startOffset은 0 이상이어야 합니다.")
	int startOffset;

	@Positive(message = "endOffset은 양수여야 합니다.")
	int endOffset;

	@JsonCreator
	public ScrapRequest(
		@JsonProperty("paragraphIndex") int paragraphIndex,
		@JsonProperty("snippetText") String snippetText,
		@JsonProperty("startOffset") int startOffset,
		@JsonProperty("endOffset") int endOffset
	) {
		this.paragraphIndex = paragraphIndex;
		this.snippetText = snippetText;
		this.startOffset = startOffset;
		this.endOffset = endOffset;
	}
}