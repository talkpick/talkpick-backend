package com.likelion.backendplus4.talkpick.backend.news.info.exception.error;

import org.springframework.http.HttpStatus;

import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 뉴스 정보 처리 과정에서 발생할 수 있는 예외 상황을 정의한 열거형 클래스입니다.
 * 각 예외는 HTTP 상태 코드, 고유 코드 번호, 메시지를 포함합니다.
 *
 * @since 2025-05-14
 */
@Getter
@RequiredArgsConstructor
public enum NewsInfoErrorCode implements ErrorCode {
	NON_UNIQUE_NEWS_INFO(HttpStatus.INTERNAL_SERVER_ERROR, 450001, "고유한 뉴스 ID 값이 아닙니다");

	private final HttpStatus status;
	private final int code;
	private final String message;

	/**
	 * 예외에 대응하는 HTTP 상태 코드를 반환합니다.
	 *
	 * @author 함예정
	 * @since 2025-05-14
	 */
	@Override
	public HttpStatus httpStatus() {
		return status;
	}

	/**
	 * 예외에 대한 고유 코드 번호를 반환합니다.
	 *
	 * @return 예외 코드 번호
	 * @author 함예정
	 * @since 2025-05-14
	 */
	@Override
	public int codeNumber() {
		return code;
	}

	/**
	 * 예외 메시지를 반환합니다.
	 *
	 * @return 예외 메시지
	 * @author 함예정
	 * @since 2025-05-14
	 */
	@Override
	public String message() {
		return message;
	}
}