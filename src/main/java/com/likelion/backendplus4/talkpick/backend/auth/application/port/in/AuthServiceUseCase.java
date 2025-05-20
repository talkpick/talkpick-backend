package com.likelion.backendplus4.talkpick.backend.auth.application.port.in;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.res.TokenResDto;

/**
 * 인증 관련 비즈니스 로직을 수행하는 유스케이스
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
	 * 계정의 중복 검사를 수행합니다.
	 *
	 * @param account 검사할 계정
	 * @author 박찬병
	 * @since 2025-05-18
	 * @modified 2025-05-18
	 */
	void checkDuplicateAccount(String account);

	/**
	 * 이메일의 중복 검사를 수행합니다.
	 *
	 * @param email 검사할 이메일
	 * @author 박찬병
	 * @since 2025-05-18
	 * @modified 2025-05-18
	 */
	void checkEmailDuplicationAndSendCode(String email);

	/**
	 * 닉네임의 중복 검사를 수행합니다.
	 *
	 * @param nickname 검사할 닉네임
	 * @author 박찬병
	 * @since 2025-05-18
	 * @modified 2025-05-18
	 */
	void checkDuplicateNickname(String nickname);

	/**
	 * 사용자를 인증하고 액세스·리프레시 토큰을 발급합니다.
	 *
	 * 1. 계정과 비밀번호로 Authentication 객체 생성
	 * 2. Authentication 기반으로 토큰 쌍(TokenPair) 생성
	 * 3. TokenPair를 TokenDto로 변환하여 반환
	 *
	 * @param account  로그인 계정
	 * @param password 로그인 비밀번호
	 * @return 발급된 토큰 정보를 담은 TokenDto
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	TokenResDto signIn(String account, String password);

	/**
	 * 리프레시 토큰을 검증하고 새로운 액세스·리프레시 토큰을 발급합니다.
	 *
	 * @param refreshToken 클라이언트로부터 전달받은 리프레시 토큰
	 * @return 갱신된 토큰 정보를 담은 TokenDto
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	TokenResDto refreshToken(String refreshToken);

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
	 * 이메일 인증 코드를 검증합니다.
	 *
	 * @param email 인증할 이메일 주소
	 * @param code 사용자가 입력한 인증 코드
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	void confirmCode(String email,String code);

	/**
	 * 사용자의 이름과 이메일을 기반으로 계정을 조회한 후,
	 * 복구용 인증 코드를 이메일로 발송하고, 인증 코드와 계정을 함께 저장합니다.
	 *
	 * @param name 사용자의 이름
	 * @param email 계정 복구를 요청한 이메일 주소
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	void storeAccountAndSendRecoveryCode(String name, String email);

	/**
	 * 이메일과 인증 코드를 검증하여 계정을 복구합니다.
	 *
	 * @param email 인증할 이메일 주소
	 * @param code 사용자에게 발송된 인증 코드
	 * @return 복구된 계정 아이디
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	String recoveryAccount(String email, String code);
}
