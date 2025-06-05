package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.filter;

import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.JwtAuthentication;
import com.likelion.backendplus4.talkpick.backend.common.util.security.TokenExtractUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 모든 요청마다 1회 실행되어 JWT 토큰을 검사하고
 * 유효할 경우 SecurityContext에 인증 정보를 설정하는 필터.
 *
 * @since 2025-05-12
 * @modified 2025-05-19
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String[] EXCLUDE_URLS = { "/auth", "/public", "/actuator" };
    private final JwtAuthentication jwtAuthentication;

    /**
     * HTTP 요청을 가로채 JWT 인증 과정을 수행합니다.
     *
     * 1. 요청 헤더에서 토큰 추출
     * 2. 토큰 유효성 검증 및 Authentication 획득
     * 3. SecurityContext에 Authentication 설정
     *
     * @param request     HTTP 요청 객체
     * @param response    HTTP 응답 객체
     * @param filterChain 필터 체인
     * @throws ServletException 필터 처리 중 예외 발생 시
     * @throws IOException      입출력 예외 발생 시
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain)
        throws ServletException, IOException {
        String token = TokenExtractUtil.extractToken(request);
        if (token != null) {
            Authentication auth = jwtAuthentication.validateAndGetAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * 특정 URL 경로에서는 이 필터를 적용하지 않습니다.
     *
     * @param request HTTP 요청 객체
     * @return 필터를 적용하지 않을 경우 true
     * @author 박찬병
     * @since 2025-05-16
     * @modified 2025-05-19
     * 2025-05-19 - 필터 제외 URL 추가
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldSkip = Arrays.stream(EXCLUDE_URLS).anyMatch(path::startsWith);

        if (path.startsWith("/actuator")) {
            System.out.println("Actuator request detected: " + path + ", shouldSkip: " + shouldSkip);
        }

        return Arrays.stream(EXCLUDE_URLS).anyMatch(path::startsWith);
    }

}
