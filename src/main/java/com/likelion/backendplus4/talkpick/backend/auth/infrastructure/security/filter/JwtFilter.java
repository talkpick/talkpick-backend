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

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtAuthentication jwtAuthentication;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에서 JWT 토큰을 추출
        String token = extractToken(request);

        if (token != null) {
			Authentication auth = jwtAuthentication.validateAndGetAuthentication(token);
			SecurityContextHolder.getContext().setAuthentication(auth);
		}

        // 필터 체인의 다음 필터로 요청과 응답을 전달
        filterChain.doFilter(request, response);
    }

    /**
     * 요청 헤더에서 JWT 토큰을 추출하는 메서드
     *
     * @param request HTTP 요청
     * @return 추출된 JWT 토큰 (없으면 null 반환)
     */
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

}

