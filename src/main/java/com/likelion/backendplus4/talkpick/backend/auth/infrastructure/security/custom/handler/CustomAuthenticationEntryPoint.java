package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.handler;

import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 인가 예외 핸들러
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) {

        log.warn("Authentication Failed: URI = {}, Method = {}, Message = {}",
            request.getRequestURI(),
            request.getMethod(),
            authException.getMessage());

        throw new AuthException(AuthErrorCode.AUTHENTICATION_FAILED, authException);
    }
}
