package com.likelion.backendplus4.talkpick.backend.news.info.exception;

import com.likelion.backendplus4.talkpick.backend.common.exception.CustomException;
import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

/**
 * 뉴스 기사 수집 관련 예외처리 클래스
 *
 * @since 2025-05-10
 * @modified 2025-05-11
 *   - 클래스에서 저자 삭제 (메소드에 저자 추가)
 *   - 코드 컨벤션에 맞도록 CustomException 상속으로 변경 (변경 전: RuntimeException 상속)
 */
public class NewsInfoException extends CustomException {
	private final ErrorCode errorCode;

	public NewsInfoException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	public NewsInfoException(ErrorCode errorCode, Throwable cause) {
		super(errorCode);
		this.errorCode = errorCode;
	}
	
	@Override
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}