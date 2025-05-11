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
