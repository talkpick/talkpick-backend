package com.likelion.backendplus4.talkpick.backend.common.configuration.security;

import java.security.Key;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


/**
 * JWT 서명 키 생성을 위한 설정 클래스입니다.
 *
 * @since 2025-05-13
 * @modified 2025-05-13
 */
@Configuration
public class JwtConfig {

	@Value("${jwt.secret}")
	private String secretKey;

	/**
	 * JWT 서명에 사용할 Key를 생성합니다.
	 *
	 * @return HMAC SHA 키
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-13
	 */
	@Bean
	public Key jwtSigningKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
