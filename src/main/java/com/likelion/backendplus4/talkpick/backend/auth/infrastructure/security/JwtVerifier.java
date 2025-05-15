package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security;

import java.security.Key;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT 토큰의 유효성을 검증하고 파싱된 Claims를 반환하는 컴포넌트.
 *
 * @author 박찬병
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtVerifier {

	private final Key jwtSigningKey;

	/**
	 * JWT 토큰을 검증하고 Claims를 반환합니다.
	 *
	 * @param token 검증할 JWT 토큰 문자열
	 * @return 파싱된 Claims 객체
	 * @throws ExpiredJwtException 토큰 만료 시 발생
	 * @throws BadCredentialsException 형식이 올바르지 않거나 서명이 유효하지 않을 때 발생
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	public Claims verifyToken(String token) {
		return parseClaims(token);
	}

	/**
	 * 실제 토큰 파싱 및 예외 처리 로직을 수행합니다.
	 *
	 * 1. 서명 키로 토큰 파싱
	 * 2. ExpiredJwtException은 그대로 던짐
	 * 3. 그 외 JwtException, IllegalArgumentException은 BadCredentialsException으로 변환
	 *
	 * @param token 파싱할 JWT 토큰 문자열
	 * @return 파싱된 Claims 객체
	 * @throws ExpiredJwtException 토큰 만료 시 발생
	 * @throws BadCredentialsException 기타 JWT 오류 발생 시 변환하여 던짐
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	private Claims parseClaims(String token) {
		try {
			return Jwts.parserBuilder()
				.setSigningKey(jwtSigningKey)
				.build()
				.parseClaimsJws(token)
				.getBody();
		} catch (ExpiredJwtException ex) {
			throw ex;
		} catch (JwtException | IllegalArgumentException ex) {
			throw new BadCredentialsException("유효하지 않은 JWT 토큰입니다.", ex);
		}
	}
}