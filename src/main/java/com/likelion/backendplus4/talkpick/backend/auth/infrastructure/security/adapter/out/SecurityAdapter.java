package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.adapter.out;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.security.core.AuthenticationException;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.SecurityPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo.TokenInfo;
import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.JwtProvider;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.JwtVerifier;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.TokenVoMapper;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;

/**
 * Spring Security와 JWT provider를 이용해 인증 및 토큰 발급·파싱 기능을 제공하는 어댑터 구현체.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Component
@RequiredArgsConstructor
public class SecurityAdapter implements SecurityPort {
	private final AuthenticationManager authManager;
	private final PasswordEncoder encoder;
	private final JwtProvider jwtProvider;
	private final JwtVerifier jwtVerifier;

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
	@Override
	@EntryExitLog
	public Authentication authenticate(String account, String rawPassword) {
		return performAuthentication(account, rawPassword);
	}


	/**
	 * 평문 비밀번호를 인코더로 암호화합니다.
	 *
	 * @param rawPassword 평문 비밀번호
	 * @return 암호화된 비밀번호 문자열
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	@EntryExitLog
	public String encodePassword(String rawPassword) {
		return encoder.encode(rawPassword);
	}

	/**
	 * 인증 정보를 바탕으로 액세스·리프레시 토큰을 생성합니다.
	 *
	 * @param authentication 인증된 Authentication 객체
	 * @return 발급된 토큰 쌍(TokenPair)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	@EntryExitLog
	public TokenPair issueToken(Authentication authentication) {
		return jwtProvider.generateToken(authentication);
	}

	/**
	 * 리프레시 토큰을 검증하고 새로운 액세스·리프레시 토큰을 생성합니다.
	 *
	 * @param refreshToken 클라이언트가 제출한 리프레시 토큰
	 * @return 갱신된 토큰 쌍(TokenPair)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	@EntryExitLog
	public TokenPair refreshToken(String refreshToken) {
		return jwtProvider.refreshAccessToken(refreshToken);
	}

	/**
	 * 액세스 토큰에서 만료 시간과 사용자 ID를 추출하여 TokenInfo VO로 반환합니다.
	 *
	 * @param accessToken 클라이언트가 제출한 액세스 토큰
	 * @return 파싱된 토큰 정보(TokenInfo)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-15
	 * 2025-05-15 닉네임 값 추가
	 */
	@Override
	@EntryExitLog
	public TokenInfo parseTokenInfo(String accessToken) {
		Claims claims = jwtVerifier.verifyToken(accessToken);

		long expiration = jwtProvider.getExpiration(claims);
		String userId = jwtProvider.getUserIdFromToken(claims);
		String nickName = jwtProvider.getNickNameFromToken(claims);

		return TokenVoMapper.toVo(expiration, userId, nickName);
	}

	/**
	 * 이메일을 기반으로 임시 토큰을 발급합니다.
	 *
	 * 비밀번호 재설정 등의 절차에서 인증이 완료된 사용자에게
	 * 임시로 사용할 수 있는 토큰을 발급할 때 사용됩니다.
	 *
	 * @param email 인증이 완료된 사용자 이메일
	 * @return tempToken 발급된 임시 토큰
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@Override
	public String issueTempToken(String email) {
		return jwtProvider.generateTempToken(email);
	}

	/**
	 * 내부적으로 AuthenticationManager를 호출하여 인증을 수행합니다.
	 * 실패 시 AuthException으로 변환하여 던집니다.
	 *
	 * @param account     로그인 계정
	 * @param rawPassword 로그인 비밀번호
	 * @return 인증된 Authentication 객체
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@EntryExitLog
	private Authentication performAuthentication(String account, String rawPassword) {
		try {
			return authManager.authenticate(
				new UsernamePasswordAuthenticationToken(account, rawPassword)
			);
		} catch (AuthenticationException ex) {
			throw new AuthException(AuthErrorCode.AUTHENTICATION_FAILED, ex);
		}
	}

}