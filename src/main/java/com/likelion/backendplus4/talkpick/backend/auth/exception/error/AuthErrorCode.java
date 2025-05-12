package com.likelion.backendplus4.talkpick.backend.auth.exception.error;

import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    AUTHENTICATION_FAILED(HttpStatus.UNAUTHORIZED, 220001, "인증에 실패하였습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, 220002, "유효하지 않은 리프레시 토큰입니다.");


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
