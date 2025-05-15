package com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.res;

import java.time.LocalDate;

import lombok.Builder;

@Builder
public record UserInfoDto(
	Long userId,
	String gender,
	LocalDate birthday,
	String name,
	String nickName,
	String email
) {

}
