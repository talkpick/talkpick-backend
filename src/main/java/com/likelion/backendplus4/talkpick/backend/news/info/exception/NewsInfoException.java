package com.likelion.backendplus4.talkpick.backend.news.info.exception;

import com.likelion.backendplus4.talkpick.backend.common.exception.CustomException;
import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

/**
 * 뉴스 정보 처리 중 발생하는 예외를 처리하기 위한 커스텀 예외 클래스입니다.
 * ErrorCode를 통해 예외의 세부 정보를 전달합니다.
 *
 * @since 2025-05-14
 */
public class NewsInfoException extends CustomException {
	private final ErrorCode errorCode;

	/**
	 * 지정된 에러 코드로 예외를 생성합니다.
	 *
	 * @param errorCode 발생한 예외에 해당하는 에러 코드
	 * @author 함예정
	 * @since 2025-05-14
	 */
	public NewsInfoException(ErrorCode errorCode) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	/**
	 * 지정된 에러 코드와 원인 예외로 예외를 생성합니다.
	 *
	 * @param errorCode 발생한 예외에 해당하는 에러 코드
	 * @param cause     원인 예외
	 * @author 함예정
	 * @since 2025-05-14
	 */
	public NewsInfoException(ErrorCode errorCode, Throwable cause) {
		super(errorCode);
		this.errorCode = errorCode;
	}

	/**
	 * 예외에 해당하는 에러 코드를 반환합니다.
	 *
	 * @return 에러 코드
	 * @author 함예정
	 * @since 2025-05-14
	 */
  
	@Override
	public ErrorCode getErrorCode() {
		return errorCode;
	}
}