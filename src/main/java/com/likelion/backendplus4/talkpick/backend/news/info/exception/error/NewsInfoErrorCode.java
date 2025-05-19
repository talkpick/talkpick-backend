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
	NON_UNIQUE_NEWS_INFO(HttpStatus.INTERNAL_SERVER_ERROR, 450001, "고유한 뉴스 ID 값이 아닙니다"),
	NEWS_INFO_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, 450002, "뉴스 정보를 찾을수 없습니다"),

	// 조회수 관련 에러 코드
	VIEW_COUNT_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450010, "조회수 동기화 처리 중 오류가 발생했습니다"),
	VIEW_COUNT_INVALID_FORMAT(HttpStatus.BAD_REQUEST, 450011, "조회수 값의 형식이 올바르지 않습니다"),
	VIEW_COUNT_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450012, "조회수 업데이트 처리 중 오류가 발생했습니다"),
	VIEW_COUNT_CLEANUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450013, "오래된 조회수 데이터 정리 중 오류가 발생했습니다");


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