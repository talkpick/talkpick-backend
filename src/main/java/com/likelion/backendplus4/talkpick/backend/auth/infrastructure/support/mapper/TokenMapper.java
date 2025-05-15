package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;

/**
 * TokenPair 도메인 모델 매핑 클래스입니다.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public class TokenMapper {

	/**
	 * 액세스 토큰과 리프레시 토큰을 TokenPair로 변환합니다.
	 *
	 * @param accessToken  액세스 토큰 문자열
	 * @param refreshToken 리프레시 토큰 문자열
	 * @return TokenPair 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	public static TokenPair toDomain(String accessToken, String refreshToken) {
		return TokenPair.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}
}