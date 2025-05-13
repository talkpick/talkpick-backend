package com.likelion.backendplus4.talkpick.backend.auth.application.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.in.AuthServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.AuthTokenStorePort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.SecurityPort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserRepositoryPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo.TokenInfo;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.res.TokenResDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.mapper.TokenDtoMapper;

import lombok.RequiredArgsConstructor;

/**
 * 인증 관련(회원가입, 로그인, 토큰 갱신, 로그아웃, 회원 삭제) 비즈니스 로직을 수행하는 서비스 구현체.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceUseCase {

	private final AuthTokenStorePort authTokenStorePort;
	private final UserRepositoryPort userRepositoryPort;
	private final SecurityPort securityPort;

	/**
	 * 신규 회원을 등록합니다.
	 *
	 * 1. 계정 중복 여부 검증
	 * 2. 비밀번호 인코딩
	 * 3. 회원 정보 저장
	 *
	 * @param authUser 회원 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	public void signUp(AuthUser authUser) {
		validateAccountNotExists(authUser.getAccount());
		applyPasswordEncording(authUser);
		userRepositoryPort.saveUser(authUser);
	}

	/**
	 * 사용자를 인증하고 액세스·리프레시 토큰을 발급합니다.
	 *
	 * 1. 계정과 비밀번호로 Authentication 객체 생성
	 * 2. Authentication 기반으로 토큰 쌍(TokenPair) 생성
	 * 3. TokenPair를 TokenDto로 변환하여 반환
	 * *
	 * @param account  로그인 계정
	 * @param password 로그인 비밀번호
	 * @return 발급된 토큰 정보를 담은 TokenDto
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	public TokenResDto signIn(String account, String password) {
		Authentication auth = securityPort.authenticate(account, password);
		TokenPair pair = securityPort.issueToken(auth);
		return TokenDtoMapper.toDto(pair);
	}

	/**
	 * 리프레시 토큰을 검증하고 새로운 액세스 토큰을 발급합니다.
	 *
	 * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰
	 * @return 갱신된 토큰 정보를 담은 TokenDto
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	public TokenResDto refreshToken(String refreshToken) {
		TokenPair tokenPair = securityPort.refreshToken(refreshToken);
		return TokenDtoMapper.toDto(tokenPair);
	}

	/**
	 * 액세스 토큰을 파싱하여 TokenInfo VO를 생성하고,
	 * 만료되지 않았을 경우에만 Redis 블랙리스트에 등록합니다.
	 *
	 * @param accessToken 클라이언트로부터 전달받은 액세스 토큰
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	public void logout(String accessToken) {
		TokenInfo tokenInfo = securityPort.parseTokenInfo(accessToken);
		performLogoutIfValid(accessToken, tokenInfo);
	}

	/**
	 * 회원 정보를 영구 삭제합니다.
	 *
	 * @param id 삭제할 회원의 고유 식별자
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	public void deleteUser(Long id) {
		userRepositoryPort.deleteUser(id);
	}

	/**
	 * 계정 중복 여부를 확인합니다.
	 *
	 * @param account 검사할 계정
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	private void validateAccountNotExists(String account) {
		userRepositoryPort.existsByAccountAndEmail(account);
	}

	/**
	 * AuthUser의 비밀번호를 인코딩하여 설정합니다.
	 *
	 * @param authUser 비밀번호를 인코딩할 대상 AuthUser
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	private void applyPasswordEncording(AuthUser authUser) {
		String encoded = securityPort.encodePassword(authUser.getPassword());
		authUser.updateEncodedPassword(encoded);
	}

	/**
	 * 토큰이 만료되지 않은 경우에만 Redis에 블랙리스트로 등록합니다.
	 *
	 * @param rawToken  원본 액세스 토큰
	 * @param tokenInfo 파싱된 토큰 정보 VO
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	private void performLogoutIfValid(String rawToken, TokenInfo tokenInfo) {
		if (!tokenInfo.isExpired()) {
			authTokenStorePort.logoutTokens(
				rawToken,
				tokenInfo.getExpirationMillis(),
				tokenInfo.getUserId()
			);
		}
	}
}