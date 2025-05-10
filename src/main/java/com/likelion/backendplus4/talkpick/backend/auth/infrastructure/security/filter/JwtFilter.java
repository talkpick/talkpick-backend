package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.filter;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.RedisAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
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

    private final TokenProvider tokenProvider;
    private final RedisAuthPort redisRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에서 JWT 토큰을 추출
        String token = extractToken(request);

        try {
            // 토큰이 존재하면 검증을 진행
            if (token != null) {
                // JWT 토큰의 유효성을 검증
                tokenProvider.validateToken(token);

                // 토큰이 블랙리스트에 포함되어 있지 않은 경우
                if (!isTokenBlacklisted(token)) {
                    // 토큰에서 인증 정보를 가져와 SecurityContext에 설정
                    Authentication authentication = tokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (ExpiredJwtException e) {
            // 만료된 토큰 예외 발생 시 로그 기록
            log.warn("만료된 토큰: {}", e.getMessage());
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
        return (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) ?
                bearerToken.substring(BEARER_PREFIX.length()) : null;
    }

    /**
     * Redis 블랙리스트에서 토큰의 존재 여부를 확인하는 메서드
     *
     * @param token JWT 토큰
     * @return 토큰이 블랙리스트에 있으면 true 반환
     */
    private boolean isTokenBlacklisted(String token) {
        String userId = tokenProvider.getUserIdFromToken(token);
        return redisRepository.isValidRefreshToken(userId, token);
    }

}

