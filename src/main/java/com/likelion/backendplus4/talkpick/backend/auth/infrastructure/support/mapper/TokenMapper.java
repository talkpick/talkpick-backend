package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;

public class TokenMapper {

	public static TokenPair toDomain(String accessToken, String refreshToken) {
		return TokenPair.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}
