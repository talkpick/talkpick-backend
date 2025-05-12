package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto;

public record TokenDto(
	String accessToken,
	String refreshToken
) {
}
