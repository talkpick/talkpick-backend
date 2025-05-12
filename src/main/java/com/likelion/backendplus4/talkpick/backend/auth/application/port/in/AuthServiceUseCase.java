package com.likelion.backendplus4.talkpick.backend.auth.application.port.in;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.TokenDto;

/**
 * 인증 관련(회원가입, 로그인, 토큰 갱신, 로그아웃, 회원 삭제) 비즈니스 로직을 수행하는 서비스 구현체.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public interface AuthServiceUseCase {

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
	void signUp(AuthUser authUser);

	/**
	 * 사용자를 인증하고 액세스·리프레시 토큰을 발급합니다.
	 *
	 * @param account  로그인 계정
	 * @param password 로그인 비밀번호
	 * @return 발급된 토큰 정보를 담은 TokenDto
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	TokenDto signIn(String account, String password);

	/**
	 * 리프레시 토큰을 검증하고 새로운 액세스·리프레시 토큰을 발급합니다.
	 *
	 * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰
	 * @return 갱신된 토큰 정보를 담은 TokenDto
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	TokenDto refreshToken(String refreshToken);

	/**
	 * 액세스 토큰을 파싱하여 TokenInfo VO를 생성하고,
	 * 만료되지 않았을 경우에만 Redis 블랙리스트에 등록합니다.
	 *
	 * @param accessToken 클라이언트로부터 전달받은 액세스 토큰
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	void logout(String accessToken);

	/**
	 * 회원 정보를 영구 삭제합니다.
	 *
	 * @param id 삭제할 회원의 고유 식별자
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	void deleteUser(Long id);

}
