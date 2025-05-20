package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.recovery;

public record RecoveryPasswordDto(
	String email,
	String code,
	String newPassword
) {
}
