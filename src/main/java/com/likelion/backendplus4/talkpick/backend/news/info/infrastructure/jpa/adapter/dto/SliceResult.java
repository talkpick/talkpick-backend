package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto;

import java.util.List;

public record SliceResult<T>(
	List<T> content,
	boolean hasNext
) {}