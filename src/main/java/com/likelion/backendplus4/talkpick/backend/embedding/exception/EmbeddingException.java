package com.likelion.backendplus4.talkpick.backend.embedding.exception;

import com.likelion.backendplus4.talkpick.backend.common.exception.CustomException;
import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

public class EmbeddingException extends CustomException {
	private final ErrorCode errorCode;

	public EmbeddingException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public EmbeddingException(ErrorCode errorCode, Throwable cause) {
		super(errorCode, cause);
		this.errorCode = errorCode;
	}

	@Override
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
