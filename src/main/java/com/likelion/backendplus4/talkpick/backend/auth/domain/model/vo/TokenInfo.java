package com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo;

import java.util.Objects;

import lombok.Getter;

@Getter
public class TokenInfo {

	private final long expirationMillis;
	private final String userId;

	public TokenInfo(long expirationMillis, String userId) {
		this.expirationMillis = expirationMillis;
		this.userId = Objects.requireNonNull(userId, "userId는 null일 수 없습니다.");
	}

	/**
	 * 현재 시각이 만료 시각을 지났는지 여부를 반환합니다.
	 *
	 * @return 만료되었으면 true, 아니면 false
	 */
	public boolean isExpired() {
		return System.currentTimeMillis() > expirationMillis;
	}
}
