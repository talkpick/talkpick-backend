package com.likelion.backendplus4.talkpick.backend.auth.presentation.dto;


public record SignUpDto(
        String account,
        String password,
		String name,
		String nickName,
		String email
) {

}
