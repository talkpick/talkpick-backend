package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.handler;

import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * 인증 진입 시점에 예외가 발생했을 때 처리하는 핸들러.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 인증 실패 시 호출됩니다.
     *
     * 1. 요청 URI, 메서드, 예외 메시지를 로그에 기록
     * 2. AuthException(AuthErrorCode.AUTHENTICATION_FAILED) 예외를 던져 일괄 예외 처리 핸들러로 전달
     *
     * @param request       클라이언트 요청 객체
     * @param response      클라이언트 응답 객체
     * @param authException 발생한 AuthenticationException
     * @throws AuthException 인증 실패를 알리기 위해 발생시킵니다.
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
        AuthenticationException authException) {

        log.warn("Authentication Failed: URI = {}, Method = {}, Message = {}",
            request.getRequestURI(),
            request.getMethod(),
            authException.getMessage());

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}