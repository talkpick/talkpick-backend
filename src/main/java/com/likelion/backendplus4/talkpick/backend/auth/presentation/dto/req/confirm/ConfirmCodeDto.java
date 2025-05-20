package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.confirm;

/**
 * 이메일 인증 코드를 확인하기 위한 요청 DTO입니다.
 *
 * @since 2025-05-20
 */
public record ConfirmCodeDto(
	String email,
	String code
) {
}
