package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.res;

import lombok.Builder;

/**
 * 액세스 토큰과 리프레시 토큰을 포함하는 응답용 DTO.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Builder
public record TokenResDto(
	String accessToken,
	String refreshToken,
	String nickname
) {
}
