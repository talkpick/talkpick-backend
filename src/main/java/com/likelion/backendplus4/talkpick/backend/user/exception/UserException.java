package com.likelion.backendplus4.talkpick.backend.user.exception;

import com.likelion.backendplus4.talkpick.backend.common.exception.CustomException;
import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

public class UserException extends CustomException {

  private final ErrorCode errorCode;

  public UserException(ErrorCode errorCode) {
    super(errorCode);
    this.errorCode = errorCode;
  }

  public UserException(ErrorCode errorCode, Throwable cause) {
    super(errorCode, cause);
    this.errorCode = errorCode;
  }

  @Override
  public ErrorCode getErrorCode() {
    return null;
  }
}

