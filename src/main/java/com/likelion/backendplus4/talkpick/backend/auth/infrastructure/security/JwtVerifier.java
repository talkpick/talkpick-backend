package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security;

import java.security.Key;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtVerifier {

	private final Key jwtSigningKey;

	public Claims verifyToken(String token) {
		return parseClaims(token);
	}

	/**
	 * 실제 토큰 파싱 및 예외 처리 로직
	 */
	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(jwtSigningKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException ex) {
			// 만료 예외는 상위로 그대로 던짐
			throw ex;
		} catch (JwtException | IllegalArgumentException ex) {
			// 그 외 JWT 오류는 BadCredentials로 변환
			throw new BadCredentialsException("유효하지 않은 JWT 토큰입니다.", ex);
		}
	}
}
