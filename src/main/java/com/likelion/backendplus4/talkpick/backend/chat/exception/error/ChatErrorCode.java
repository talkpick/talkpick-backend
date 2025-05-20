package com.likelion.backendplus4.talkpick.backend.chat.exception.error;

import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
/**
 * 에러 코드 인터페이스 각 에러 항목에 대한 HTTP 상태, 에러 번호, 메시지를 제공한다.
 * A[BB][CCC]
 * A (1자리) : 에러 심각도 (1~5)
 * 1: 클라이언트 오류
 * 2: 인증 관련 오류
 * 3: 사용자 관련 오류
 * 4: 서버 오류
 * 5: 시스템 오류
 *
 * BB (2자리) : 도메인 코드
 * 10: 사용자 관련 (ex: USER_NOT_FOUND)
 * 20: 인증 관련 (ex: AUTHORIZATION_FAILED)
 * 30: DB 관련 오류 (ex: DB_CONNECTION_FAILED)
 * 40: API 관련 오류 (ex: API_TIMEOUT)
 * 50: 시스템 오류 (ex: INTERNAL_SERVER_ERROR)
 *
 * CCC (3자리) : 세부 오류 순번
 * 001: 첫 번째 오류
 * 002: 두 번째 오류
 * 003: 세 번째 오류, 등등
 *
 * @modified 2025-04-18
 * @since 2025-04-16
 */
@RequiredArgsConstructor
public enum ChatErrorCode implements ErrorCode {
    INVALID_MESSAGE_TYPE(HttpStatus.BAD_REQUEST, 140001, "유효하지 않은 메시지 타입입니다."),
    INVALID_ARTICLE_ID(HttpStatus.BAD_REQUEST, 140002, "article id는 비어있을 수 없습니다."),
    INVALID_SENDER(HttpStatus.BAD_REQUEST, 140003, "sender는 비어있을 수 없습니다.");



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
