package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.support;

import org.springframework.data.domain.Slice;

import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.adapter.dto.SliceResult;

public class SliceResultBuilder {
	public static <T> SliceResult<T> createSliceResult(Slice<T> slice) {
		return new SliceResult<>(slice.getContent(), slice.hasNext());
	}
}
