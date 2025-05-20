package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.confirm;

public record ConfirmCodeDto(
	String email,
	String code
) {
}
