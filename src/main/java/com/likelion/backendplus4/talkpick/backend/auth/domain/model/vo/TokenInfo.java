package com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo;

import java.util.Objects;
import lombok.Getter;

/**
 * 토큰의 만료 시각과 사용자 식별자를 보유하는 값 객체.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Getter
public class TokenInfo {

	private final long expirationMillis;
	private final String userId;

	/**
	 * TokenInfo 인스턴스를 생성합니다.
	 *
	 * @param expirationMillis 토큰 만료 시각 (밀리초)
	 * @param userId           토큰에 포함된 사용자 식별자
	 * @throws NullPointerException userId가 null일 경우
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	public TokenInfo(long expirationMillis, String userId) {
		this.expirationMillis = expirationMillis;
		this.userId = Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
	}

	/**
	 * 토큰이 만료되었는지 여부를 반환합니다.
	 *
	 * @return 만료되었으면 true, 아니면 false
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	public boolean isExpired() {
		return 0 >= expirationMillis;
	}
}