package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req;

/**
 * 로그인 요청 정보를 담는 DTO.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public record SignInDto(
	String account,
	String password
) { }