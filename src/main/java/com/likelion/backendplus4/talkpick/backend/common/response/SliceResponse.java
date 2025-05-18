package com.likelion.backendplus4.talkpick.backend.common.response;

import java.util.List;

public record SliceResponse<T>(
	List<T> items,
	boolean hasNext) {
}