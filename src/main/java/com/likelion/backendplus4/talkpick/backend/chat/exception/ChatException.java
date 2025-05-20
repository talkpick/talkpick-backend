package com.likelion.backendplus4.talkpick.backend.chat.exception;

import com.likelion.backendplus4.talkpick.backend.common.exception.CustomException;
import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

public class ChatException extends CustomException {
	private final ErrorCode errorCode;

	public ChatException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	@Override
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
