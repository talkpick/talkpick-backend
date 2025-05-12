package com.likelion.backendplus4.talkpick.backend.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * 액세스 토큰과 리프레시 토큰으로 구성된 토큰 쌍을 나타내는 도메인 모델.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Builder
@AllArgsConstructor
@Getter
public class TokenPair {

	private String accessToken;
	private String refreshToken;

}
