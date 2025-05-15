package com.likelion.backendplus4.talkpick.backend.auth.exception;

import com.likelion.backendplus4.talkpick.backend.common.exception.CustomException;
import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

public class AuthException extends CustomException {

    private final ErrorCode errorCode;

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
        this.errorCode = errorCode;
    }

    public AuthException(ErrorCode errorCode, Throwable cause) {
        super(errorCode, cause);
        this.errorCode = errorCode;
    }

    @Override
    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
