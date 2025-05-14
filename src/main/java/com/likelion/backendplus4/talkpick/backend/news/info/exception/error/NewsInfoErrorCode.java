package com.likelion.backendplus4.talkpick.backend.news.info.exception.error;

import org.springframework.http.HttpStatus;

import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 뉴스 기사 수집 관련 오류 코드를 정의하는 열거형
 *
 * @since 2025-05-10
 * @modified 2025-05-11
 *   - 클래스 주석에서 저자 삭제
 *   - 예외 전역 처리를 위해 상속 구조로 변경
 *   - 예외 전역 처리로 알 수 없는 오류 코드 삭제
 */
@Getter
@RequiredArgsConstructor
public enum NewsInfoErrorCode implements ErrorCode {
	NON_UNIQUE_NEWS_INFO(HttpStatus.INTERNAL_SERVER_ERROR, 450001, "고유한 뉴스 ID 값이 아닙니다");

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