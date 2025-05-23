package com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.req;

import java.time.LocalDate;

import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Gender;

/**
 * 사용자 프로필 수정 요청 정보를 담는 DTO.
 *
 * @since 2025-05-16
 */
public record UserUpdateReqDto(
	String name,
	String nickName,
	LocalDate birthDay,
	Gender gender,
	String email
) {
}
