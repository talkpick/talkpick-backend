package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.TokenDto;

/**
 * TokenDto로 변환하는 매퍼 클래스.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public class TokenDtoMapper {

	/**
	 * TokenPair 도메인 객체에서 TokenDto 로 변환합니다.
	 *
	 * @param tokenPair 변환할 토큰 쌍 도메인 모델
	 * @return TokenDto 응답용 DTO
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	public static TokenDto toDto(TokenPair tokenPair) {
		return new TokenDto(tokenPair.getAccessToken(), tokenPair.getRefreshToken());
	}
}