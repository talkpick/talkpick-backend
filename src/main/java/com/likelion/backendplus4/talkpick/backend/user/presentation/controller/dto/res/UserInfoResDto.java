package com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.res;

import java.time.LocalDate;

import lombok.Builder;

/**
 * 사용자 정보를 응답하는 DTO.
 *
 * @since 2025-05-16
 */
@Builder
public record UserInfoResDto(
	Long userId,
	String gender,
	LocalDate birthday,
	String name,
	String nickName,
	String email
) {

}
