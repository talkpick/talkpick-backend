package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.enums.DuplicateField;

public record DuplicateCheckReqDto(
	DuplicateField field,
	String value
) {
}
