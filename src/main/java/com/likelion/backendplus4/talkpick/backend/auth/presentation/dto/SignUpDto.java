package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto;

/**
 * 회원 가입 요청 정보를 담는 DTO.
 *
 * @since 2025-05-12
 * @modified 2025-05-12 박찬병
 */
public record SignUpDto(
	String account,
	String password,
	String name,
	String nickName,
	String email
) { }