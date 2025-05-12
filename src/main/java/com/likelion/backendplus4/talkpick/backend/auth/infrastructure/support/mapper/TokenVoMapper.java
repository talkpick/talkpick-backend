package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo.TokenInfo;

public class TokenVoMapper {

	public static TokenInfo toVo(long expirationMillis, String userId) {
		return new TokenInfo(expirationMillis, userId);
	}
}
