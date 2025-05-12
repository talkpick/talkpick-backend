package com.likelion.backendplus4.talkpick.backend.auth.presentation.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.TokenDto;

public class TokenDtoMapper {

	public static TokenDto toDto(TokenPair tokenPair) {
		return new TokenDto(tokenPair.getAccessToken(), tokenPair.getRefreshToken());
	}
}
