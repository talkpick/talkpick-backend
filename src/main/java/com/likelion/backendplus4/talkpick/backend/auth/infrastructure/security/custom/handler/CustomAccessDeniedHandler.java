package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.handler;

import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 권한 예외 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException) {

        log.warn("Access Denied: URI = {}, Method = {}, Message = {}",
            request.getRequestURI(),
            request.getMethod(),
            accessDeniedException.getMessage());

        throw new AuthException(AuthErrorCode.AUTHENTICATION_FAILED, accessDeniedException);
    }
}
