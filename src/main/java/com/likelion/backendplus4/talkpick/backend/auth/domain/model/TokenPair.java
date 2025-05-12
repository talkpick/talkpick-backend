package com.likelion.backendplus4.talkpick.backend.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@AllArgsConstructor
@Getter
public class TokenPair {

	private String accessToken;
	private String refreshToken;

}
