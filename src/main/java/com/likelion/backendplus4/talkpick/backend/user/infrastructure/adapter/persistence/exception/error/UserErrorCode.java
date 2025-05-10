package com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.exception.error;

import org.springframework.http.HttpStatus;

import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserErrorCode implements ErrorCode {
	USER_NOT_FOUND(HttpStatus.NOT_FOUND, 110001, "사용자를 찾을 수 없습니다.");

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
