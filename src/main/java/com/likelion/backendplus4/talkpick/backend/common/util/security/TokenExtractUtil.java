package com.likelion.backendplus4.talkpick.backend.common.util.security;

import org.springframework.util.StringUtils;

import jakarta.servlet.http.HttpServletRequest;

public class TokenExtractUtil {

	private static final String AUTH_HEADER  = "Authorization";
	private static final String BEARER_PREFIX = "Bearer ";


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
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 */
	public static String extractToken(HttpServletRequest request) {
		String header = request.getHeader(AUTH_HEADER);
		if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
			return header.substring(BEARER_PREFIX.length());
		}
		return null;
	}

}
