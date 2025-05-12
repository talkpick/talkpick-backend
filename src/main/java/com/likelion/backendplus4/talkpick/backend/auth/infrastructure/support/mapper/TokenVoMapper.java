package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo.TokenInfo;

/**
 * TokenInfo VO 매핑 클래스입니다.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public class TokenVoMapper {

	/**
	 * 만료 시간과 사용자 ID로 TokenInfo VO를 생성합니다.
	 *
	 * @param expirationMillis 토큰 만료까지 남은 시간(밀리초)
	 * @param userId           토큰에 포함된 사용자 식별자
	 * @return TokenInfo VO 객체
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	public static TokenInfo toVo(long expirationMillis, String userId) {
		return new TokenInfo(expirationMillis, userId);
	}
}