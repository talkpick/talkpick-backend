package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.recovery;

public record RecoveryUserInfoDto(
	String email,
	String name,
	String account
) {
}
