package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.RedisAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.user.CustomUserDetails;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.CustomUserDetailsMapper;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtAuthentication {

	private final JwtVerifier jwtParser;
	private final RedisAuthPort redisAuthPort;

	/**
	 * 토큰 검증 후 Authentication 반환
	 */
	public Authentication validateAndGetAuthentication(String token) {
		// 1) 토큰 검증 및 Claims 추출
		Claims claims = jwtParser.verifyToken(token);

		// 2) 블랙리스트 체크
		if (redisAuthPort.isTokenBlacklisted(token)) {
			throw new InsufficientAuthenticationException("블랙리스트된 토큰입니다.");
		}

		// 3) 매퍼로 CustomUserDetails 생성 (roles 파싱 포함)
		CustomUserDetails userDetails = CustomUserDetailsMapper.fromClaims(claims);

		// 4) Authentication 리턴
		return new UsernamePasswordAuthenticationToken(
			userDetails,
			token,
			userDetails.getAuthorities()
		);
	}
}
