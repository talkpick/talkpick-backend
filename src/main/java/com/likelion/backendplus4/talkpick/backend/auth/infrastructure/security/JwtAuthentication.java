package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.AuthTokenStorePort;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.user.CustomUserDetails;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.CustomUserDetailsMapper;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰을 검증하고, 유효한 경우 Spring Security Authentication 객체로 변환하는 컴포넌트.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Component
@RequiredArgsConstructor
public class JwtAuthentication {

	private final JwtVerifier jwtParser;
	private final AuthTokenStorePort authTokenStorePort;

	/**
	 * JWT 토큰을 검증하고 Authentication 객체로 반환합니다.
	 *
	 * 1. JWT 파싱 및 Claims 추출
	 * 2. Redis 블랙리스트 체크
	 * 3. Claims 기반으로 CustomUserDetails 생성
	 * 4. UsernamePasswordAuthenticationToken 생성 및 반환
	 *
	 * @param token 검증할 JWT 토큰 문자열
	 * @return 인증된 Authentication 객체
	 * @throws InsufficientAuthenticationException 블랙리스트된 토큰인 경우 발생
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@EntryExitLog
	public Authentication validateAndGetAuthentication(String token) {
		Claims claims = jwtParser.verifyToken(token);

		ensureNotBlacklisted(token);

		CustomUserDetails userDetails = CustomUserDetailsMapper.fromClaims(claims);

		return new UsernamePasswordAuthenticationToken(
			userDetails,
			token,
			userDetails.getAuthorities()
		);
	}

    /**
     * 토큰 블랙리스트 여부를 확인하고, 블랙리스트된 경우 예외를 던집니다.
     *
     * @param token 검증할 JWT 토큰 문자열
     * @throws InsufficientAuthenticationException 블랙리스트된 토큰인 경우 발생
     * @since 2025-05-14
     * @modified 2025-05-14
     * @author 박찬병
     */
	@EntryExitLog
    private void ensureNotBlacklisted(String token) {
        if (authTokenStorePort.isTokenBlacklisted(token)) {
            throw new InsufficientAuthenticationException("블랙리스트된 토큰입니다.");
        }
    }
}