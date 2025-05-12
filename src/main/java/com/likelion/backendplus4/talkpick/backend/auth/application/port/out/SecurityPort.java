package com.likelion.backendplus4.talkpick.backend.auth.application.port.out;


import org.springframework.security.core.Authentication;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo.TokenInfo;

/**
 * Spring Security와 JWT provider를 이용해 인증 및 토큰 발급·파싱 기능을 제공하는 어댑터 구현체.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public interface SecurityPort {

	/**
	 * 사용자 계정과 비밀번호를 이용해 인증을 수행합니다.
	 *
	 * @param account     로그인 계정
	 * @param rawPassword 로그인 비밀번호
	 * @return 인증된 Authentication 객체
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	Authentication authenticate(String account, String rawPassword);

	/**
	 * 평문 비밀번호를 인코더로 암호화합니다.
	 *
	 * @param rawPassword 평문 비밀번호
	 * @return 암호화된 비밀번호 문자열
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	String encodePassword(String rawPassword);

	/**
	 * 인증 정보를 바탕으로 액세스·리프레시 토큰을 생성합니다.
	 *
	 * @param authentication 인증된 Authentication 객체
	 * @return 발급된 토큰 쌍(TokenPair)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	TokenPair issueToken(Authentication authentication);

	/**
	 * 리프레시 토큰을 검증하고 새로운 액세스·리프레시 토큰을 생성합니다.
	 *
	 * @param refreshToken 클라이언트가 제출한 리프레시 토큰
	 * @return 갱신된 토큰 쌍(TokenPair)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	TokenPair refreshToken(String refreshToken);

	/**
	 * 액세스 토큰에서 만료 시간과 사용자 ID를 추출하여 TokenInfo VO로 반환합니다.
	 *
	 * @param accessToken 클라이언트가 제출한 액세스 토큰
	 * @return 파싱된 토큰 정보(TokenInfo)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	TokenInfo parseTokenInfo(String accessToken);

}
