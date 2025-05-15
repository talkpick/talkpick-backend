package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req;

import java.time.LocalDate;

import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Gender;

/**
 * 회원 가입 요청 정보를 담는 DTO.
 *
 * @since 2025-05-12
 * @modified 2025-05-13
 * 2025-05-13 성별, 생년월일 추가
 */
public record SignUpDto(
	String account,
	String password,
	String name,
	String nickName,
	String email,
	Gender gender,
	LocalDate birthDay
) { }