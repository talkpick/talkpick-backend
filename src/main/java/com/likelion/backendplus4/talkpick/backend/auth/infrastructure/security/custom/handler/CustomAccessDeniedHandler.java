package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.handler;

import java.io.IOException;

import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

/**
 * 인증된 사용자가 권한이 부족한 리소스에 접근할 때 발생하는 예외를 처리합니다.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    /**
     * 권한 거부 예외 발생 시 호출됩니다.
     *
     * 1. 요청 URI, 메서드, 예외 메시지를 로그에 기록
     * 2. AuthException(AuthErrorCode.AUTHENTICATION_FAILED) 예외를 던져 일괄 예외 처리 핸들러로 전달
     *
     * @param request  클라이언트 요청 객체
     * @param response 클라이언트 응답 객체
     * @param accessDeniedException 발생한 AccessDeniedException
     * @throws AuthException 권한이 부족할 때 발생시킵니다.
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException)  {

        log.warn("Access Denied: URI = {}, Method = {}, Message = {}",
            request.getRequestURI(),
            request.getMethod(),
            accessDeniedException.getMessage());

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    }
}