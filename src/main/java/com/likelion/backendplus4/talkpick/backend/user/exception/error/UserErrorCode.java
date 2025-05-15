package com.likelion.backendplus4.talkpick.backend.user.exception.error;

import org.springframework.http.HttpStatus;

import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, 110001, "사용자를 찾을 수 없습니다."),
	ACCOUNT_DUPLICATE(HttpStatus.BAD_REQUEST, 110002, "이미 사용 중인 계정입니다.");

	private final HttpStatus status;
	private final int code;
	private final String message;

	@Override
	public HttpStatus httpStatus() {
		return status;
	}

	@Override
	public int codeNumber() {
		return code;
	}

	@Override
	public String message() {
		return message;
	}
}
