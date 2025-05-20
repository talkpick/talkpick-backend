package com.likelion.backendplus4.talkpick.backend.auth.exception.error;

import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, 220001, "인증에 실패하였습니다."),
    AUTHORIZATION_FAILED(HttpStatus.FORBIDDEN, 220002, "권한이 없습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, 220003, "유효한 액세스 토큰이 필요합니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 220004, "유효하지 않은 리프레시 토큰입니다."),
    AUTHENTICATION_REQUIRED(HttpStatus.UNAUTHORIZED, 220005, "사용자 인증 정보가 필요합니다."),
    INVALID_USER_ID_FORMAT(HttpStatus.BAD_REQUEST,    220006, "ID 값이 Long으로 변환할 수 없습니다."),

    VERIFY_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, 220007, "인증 코드가 존재하지 않거나 만료되었습니다."),
    VERIFY_CODE_MISMATCH(HttpStatus.BAD_REQUEST, 220008, "인증 코드가 일치하지 않습니다."),
    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, 220009, "계정 정보가 비어있습니다."),

    REDIS_STORE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, 430001, "Redis에 리프레시 토큰 저장에 실패했습니다."),
    REDIS_RETRIEVE_FAILURE(HttpStatus.INTERNAL_SERVER_ERROR, 430002, "Redis에서 리프레시 토큰 조회에 실패했습니다."),
    REDIS_BLACKLIST_CHECK_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 430003, "Redis에서 블랙리스트 토큰 조회에 실패했습니다."),
    REDIS_LOGOUT_PROCESS_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 430004, "Redis 로그아웃 처리에 실패했습니다."),
    REDIS_AUTHORITIES_RETRIEVE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, 430005, "Redis에서 권한 정보 조회에 실패했습니다.");



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
