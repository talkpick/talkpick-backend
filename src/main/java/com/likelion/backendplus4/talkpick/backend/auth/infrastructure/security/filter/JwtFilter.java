package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.filter;

import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.JwtAuthentication;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * 모든 요청마다 1회 실행되어 JWT 토큰을 검사하고
 * 유효할 경우 SecurityContext에 인증 정보를 설정하는 필터.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

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
        String token = extractToken(request);
        if (token != null) {
            Authentication auth = jwtAuthentication.validateAndGetAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Authorization 헤더에서 "Bearer " 접두사를 제거한 뒤 JWT 토큰을 반환합니다.
     *
     * 1. Authorization 헤더 조회
     * 2. Bearer 접두사 검사
     * 3. 접두사 제거 후 토큰 반환
     *
     * @param request HTTP 요청 객체
     * @return 추출된 JWT 토큰, 없으면 null
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}