package com.likelion.backendplus4.talkpick.backend.auth.application.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.in.AuthServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.AuthStorePort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.MailSendPort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.SecurityPort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserRepositoryPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo.TokenInfo;
import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.res.TokenResDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.mapper.TokenDtoMapper;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.util.code.CodeGenerator;

import lombok.RequiredArgsConstructor;

/**
 * 인증 관련(회원가입, 로그인, 토큰 갱신, 로그아웃, 회원 삭제) 비즈니스 로직을 수행하는 서비스 구현체.
 *
 * @since 2025-05-12
 * @modified 2025-05-20
 * 2025.05.20 아이디/비밀번호 찾기 추가
 */
@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceUseCase {

	private final AuthStorePort authStorePort;
	private final UserRepositoryPort userRepositoryPort;
	private final MailSendPort mailSendPort;
	private final SecurityPort securityPort;

	/**
	 * 신규 회원을 등록합니다.
	 *
	 * 1. 비밀번호 인코딩
	 * 2. 회원 정보 저장
	 *
	 * @param authUser 회원 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	@EntryExitLog
	public void signUp(AuthUser authUser) {
		applyPasswordEncoding(authUser);
		userRepositoryPort.saveUser(authUser);
	}

	/**
	 * 계정의 중복 검사를 수행합니다.
	 *
	 * @param account 검사할 계정
	 * @author 박찬병
	 * @since 2025-05-18
	 * @modified 2025-05-18
	 */
	@Override
	@EntryExitLog
	public void checkDuplicateAccount(String account) {
		userRepositoryPort.existsByAccount(account);
	}


	/**
	 * 이메일의 중복 검사를 수행하고, 인증 코드를 전송합니다
	 *
	 * @param email 검사할 이메일
	 * @author 박찬병
	 * @since 2025-05-18
	 * @modified 2025-05-20
	 * 2025-05-20 이메일 인증 코드 전송 구현
	 */
	@Override
	@EntryExitLog
	public void checkEmailDuplicationAndSendCode(String email) {
		userRepositoryPort.existsByEmail(email);
		String emailAuthCode = generateAndSendEmailVerifyCode(email);
		authStorePort.saveEmailAuthData(email, emailAuthCode, null);
	}

	/**
	 * 닉네임의 중복 검사를 수행합니다.
	 *
	 * @param nickname 검사할 닉네임
	 * @author 박찬병
	 * @since 2025-05-18
	 * @modified 2025-05-18
	 */
	@Override
	@EntryExitLog
	public void checkDuplicateNickname(String nickname) {
		userRepositoryPort.existsByNickname(nickname);
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
	@EntryExitLog
	public TokenResDto signIn(String account, String password) {
		Authentication auth = securityPort.authenticate(account, password);
		TokenPair pair = securityPort.issueToken(auth);
		TokenInfo tokenInfo = securityPort.parseTokenInfo(pair.getAccessToken());
		return TokenDtoMapper.toDto(pair, tokenInfo.getNickname());
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
	@EntryExitLog
	public TokenResDto refreshToken(String refreshToken) {
		TokenPair tokenPair = securityPort.refreshToken(refreshToken);
		return TokenDtoMapper.toDto(tokenPair, null);
	}

	/**
	 * 액세스 토큰을 파싱하여 TokenInfo VO를 생성하고,
	 * 만료되지 않았을 경우에만 Redis 블랙리스트에 등록합니다.
	 *
	 * @param accessToken 클라이언트로부터 전달받은 액세스 토큰
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-14
	 */
	@Override
	@EntryExitLog
	public void logout(String accessToken) {
		validateAccessToken(accessToken);
		TokenInfo tokenInfo = securityPort.parseTokenInfo(accessToken);
		performLogoutIfValid(accessToken, tokenInfo);
	}

	/**
	 * 사용자가 입력한 이메일 인증 코드를 검증합니다.
	 *
	 * @param email 인증할 이메일 주소
	 * @param code 사용자가 입력한 인증 코드
	 * @throws AuthException 인증 코드가 없거나 일치하지 않을 경우 발생
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@Override
	@EntryExitLog
	public void verifyEmailCode(String email, String code) {
		authStorePort.verifyCode(email, code);
	}

	/**
	 * 사용자의 이름과 이메일을 기반으로 계정을 조회한 후,
	 * 복구용 인증 코드를 이메일로 발송하고, 인증 코드와 계정을 함께 저장합니다.
	 *
	 * @param name 사용자의 이름
	 * @param email 계정 복구를 요청한 이메일 주소
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@Override
	public void storeAccountAndSendRecoveryCode(String name, String email) {
		String account = userRepositoryPort.findUserAccountByNameAndEmail(name, email);
		String authCode = generateAndSendEmailVerifyCode(email);
		authStorePort.saveEmailAuthData(email, authCode, account);
	}

	/**
	 * 이메일과 인증 코드를 검증하여 계정을 복구합니다.
	 *
	 * @param email 인증할 이메일 주소
	 * @param code 사용자에게 발송된 인증 코드
	 * @return 복구된 계정 아이디
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@Override
	public String recoveryAccount(String email, String code) {
		return verifyCodeAndRecoveryAccount(email, code);
	}

	/**
	 * 사용자 존재 여부를 확인하고 계정 복구를 위한 이메일 인증 코드를 전송합니다.
	 *
	 * 인증 코드는 Redis에 저장되며, 사용자 입력과의 검증에 사용됩니다.
	 *
	 * @param name 사용자 이름
	 * @param email 사용자 이메일
	 * @param account 사용자 계정 ID
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@Override
	public void findUserAndSendRecoveryCode(String name, String email, String account) {
		userRepositoryPort.validateUserExistence(name, email, account);
		String authCode = generateAndSendEmailVerifyCode(email);
		authStorePort.saveEmailAuthData(email, authCode, null);
	}


	/**
	 * 이메일 인증 코드 검증 후, 새 비밀번호로 사용자 비밀번호를 재설정합니다.
	 *
	 * 인증 코드가 올바를 경우 비밀번호를 인코딩하여 업데이트합니다.
	 *
	 * @param email 사용자 이메일
	 * @param code 이메일 인증 코드
	 * @param newPassword 새 비밀번호 (인코딩 전)
	 * @throws AuthException 인증 코드가 잘못되었을 경우
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@Override
	public void recoveryPassword(String email, String code, String newPassword) {
		authStorePort.verifyCode(email, code);
		String encodePassword = securityPort.encodePassword(newPassword);
		userRepositoryPort.updateUserPassword(email, encodePassword);
	}

	/**
	 * AuthUser의 비밀번호를 인코딩하여 설정합니다.
	 *
	 * @param authUser 비밀번호를 인코딩할 대상 AuthUser
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@EntryExitLog
	private void applyPasswordEncoding(AuthUser authUser) {
		String encoded = securityPort.encodePassword(authUser.getPassword());
		authUser.updateEncodedPassword(encoded);
	}

	/**
	 * 이메일 인증 코드를 생성하고 전송합니다.
	 *
	 * @param email 인증 코드를 전송할 이메일 주소
	 * @return emailAuthCode 생성한 인증 코드
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@EntryExitLog
	private String generateAndSendEmailVerifyCode(String email) {
		String emailAuthCode = CodeGenerator.generateCode();
		mailSendPort.sendMail(email, emailAuthCode);
		return emailAuthCode;
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
	@EntryExitLog
	private void performLogoutIfValid(String rawToken, TokenInfo tokenInfo) {
		if (!tokenInfo.isExpired()) {
			authStorePort.logoutTokens(
				rawToken,
				tokenInfo.getExpirationMillis(),
				tokenInfo.getUserId()
			);
		}
	}

	/**
	 * 액세스 토큰이 null인지 검증하고, null인 경우 예외를 던집니다.
	 *
	 * @param accessToken 클라이언트로부터 전달받은 액세스 토큰
	 * @throws AuthException 액세스 토큰이 제공되지 않은 경우 발생
	 * @since 2025-05-14
	 * @modified 2025-05-14
	 * @author 박찬병
	 */
	@EntryExitLog
	private void validateAccessToken(String accessToken) {
		if (accessToken == null) {
			throw new AuthException(AuthErrorCode.INVALID_ACCESS_TOKEN);
		}
	}

	/**
	 * 이메일과 인증 코드를 검증하여 계정을 복구합니다.
	 * 인증에 성공하면 저장된 계정을 반환하고, 실패 시 예외를 발생시킵니다.
	 *
	 * @param email 인증할 이메일 주소
	 * @param code 사용자에게 발송된 인증 코드
	 * @return 복구된 계정 아이디
	 * @throws AuthException 인증 실패 또는 계정이 존재하지 않을 경우
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private String verifyCodeAndRecoveryAccount(String email, String code) {
		return authStorePort.verifyCode(email, code).orElseThrow(
			() -> new AuthException(AuthErrorCode.ACCOUNT_NOT_FOUND)
		);
	}
}