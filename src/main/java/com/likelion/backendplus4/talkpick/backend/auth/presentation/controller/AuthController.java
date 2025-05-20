package com.likelion.backendplus4.talkpick.backend.auth.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.in.AuthServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.AuthUserMapper;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.check.CheckAccountDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.check.CheckEmailDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.check.CheckNicknameDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.confirm.ConfirmCodeDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.RefreshReqDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.SignInDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.recovery.RecoveryAccountDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.res.TokenResDto;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.common.util.security.TokenExtractUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * 인증 관련 API 요청을 처리하는 컨트롤러.
 *
 * @author 박찬병
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

	private final AuthServiceUseCase authServiceUseCase;

	/**
	 * 회원 가입 요청을 처리합니다.
	 *
	 * @param signUpDto 회원 가입 정보 DTO
	 * @return 빈 응답(ApiResponse<Void>)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@EntryExitLog
	@PostMapping("/signUp")
	public ResponseEntity<ApiResponse<Void>> signUp(@Valid @RequestBody SignUpDto signUpDto) {
		AuthUser user = AuthUserMapper.toDomainByDto(signUpDto);
		authServiceUseCase.signUp(user);
		return ApiResponse.success();
	}

	/**
	 * 로그인 요청을 처리하고 토큰을 발급합니다.
	 *
	 * @param signInDto 로그인 요청 정보 DTO
	 * @return 토큰 정보(ApiResponse<TokenDto>)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@EntryExitLog
	@PostMapping("/signIn")
	public ResponseEntity<ApiResponse<TokenResDto>> signIn(@Valid @RequestBody SignInDto signInDto) {
		TokenResDto tokenResDto = authServiceUseCase.signIn(signInDto.account(), signInDto.password());
		return ApiResponse.success(tokenResDto);
	}

	/**
	 * 로그아웃 요청을 처리합니다.
	 *
	 * @return 빈 응답(ApiResponse<Void>)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@EntryExitLog
	@PostMapping("/signOut")
	public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {
		String token = TokenExtractUtil.extractToken(request);
		authServiceUseCase.logout(token);
		return ApiResponse.success();
	}

	/**
	 * 액세스 토큰 갱신 요청을 처리합니다.
	 *
	 * @param requestToken 재발행 요청 토큰 정보 DTO
	 * @return 갱신된 토큰 정보(ApiResponse<TokenDto>)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@EntryExitLog
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<TokenResDto>> refreshAccessToken(
		@Valid @RequestBody RefreshReqDto requestToken
	) {
		TokenResDto tokenResDto = authServiceUseCase.refreshToken(requestToken.refreshToken());
		return ApiResponse.success(tokenResDto);
	}

	/**
	 * 클라이언트로부터 계정 중복 검사 요청을 받아 처리합니다.
	 *
	 * @param checkAccountDto 검사할 계정
	 * @return 빈 성공 응답(ApiResponse<Void>)
	 * @author 박찬병
	 * @since 2025-05-18
	 * @modified 2025-05-18
	 */
	@EntryExitLog
	@PostMapping("/checkDuplicate/account")
	public ResponseEntity<ApiResponse<Void>> checkDuplicateAccount(@Valid @RequestBody CheckAccountDto checkAccountDto) {
		authServiceUseCase.checkDuplicateAccount(checkAccountDto.account());
		return ApiResponse.success();
	}

	/**
	 * 클라이언트로부터 이메일 중복 검사 요청을 받아 처리하고,
	 * 이메일 인증을 위한 인증 코드를 보냅니다.
	 *
	 * @param checkEmailDto 검사할 이메일
	 * @return 빈 성공 응답(ApiResponse<Void>)
	 * @author 박찬병
	 * @since 2025-05-18
	 * @modified 2025-05-18
	 */
	@EntryExitLog
	@PostMapping("/checkDuplicate/email")
	public ResponseEntity<ApiResponse<Void>> verifyEmailDuplicationAndSendCode(@Valid @RequestBody CheckEmailDto checkEmailDto) {
		authServiceUseCase.checkEmailDuplicationAndSendCode(checkEmailDto.email());
		return ApiResponse.success();
	}


	/**
	 * 클라이언트로부터 닉네임 중복 검사 요청을 받아 처리합니다.
	 *
	 * @param checkNicknameDto 검사할 닉네임
	 * @return 빈 성공 응답(ApiResponse<Void>)
	 * @author 박찬병
	 * @since 2025-05-18
	 * @modified 2025-05-18
	 */
	@EntryExitLog
	@PostMapping("/checkDuplicate/nickname")
	public ResponseEntity<ApiResponse<Void>> checkDuplicateNickname(@Valid @RequestBody CheckNicknameDto checkNicknameDto) {
		authServiceUseCase.checkDuplicateNickname(checkNicknameDto.nickName());
		return ApiResponse.success();
	}

	/**
	 * 클라이언트가 입력한 이메일 인증 코드를 검증합니다.
	 *
	 * @param confirmDto 이메일과 인증 코드가 담긴 DTO
	 * @return 빈 성공 응답(ApiResponse<Void>)
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@EntryExitLog
	@PostMapping("/confirm/code")
	public ResponseEntity<ApiResponse<Void>> confirmCode(@RequestBody ConfirmCodeDto confirmDto) {
		authServiceUseCase.confirmCode(confirmDto.email(), confirmDto.code());
		return ApiResponse.success();
	}

	/**
	 * 계정 복구를 위한 인증 코드를 이메일로 발송합니다.
	 *
	 * @param recoveryAccountDto 사용자 이름과 이메일이 담긴 요청 DTO
	 * @return 응답 본문 없이 성공 응답 반환
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@EntryExitLog
	@PostMapping("/account/recovery/code")
	public ResponseEntity<ApiResponse<Void>> sendAccountRecoveryCode(@RequestBody RecoveryAccountDto recoveryAccountDto) {
		authServiceUseCase.storeAccountAndSendRecoveryCode(recoveryAccountDto.name(), recoveryAccountDto.email());
		return ApiResponse.success();
	}

	/**
	 * 이메일과 인증 코드를 검증하여 사용자의 계정을 반환합니다.
	 *
	 * @param confirmCodeDto 이메일과 인증 코드가 담긴 요청 DTO
	 * @return 복구된 계정 아이디 문자열을 포함한 성공 응답 반환
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	@EntryExitLog
	@PostMapping("/account/recovery/result")
	public ResponseEntity<ApiResponse<String>> recoveryAccount(@RequestBody ConfirmCodeDto confirmCodeDto) {
		String account = authServiceUseCase.recoveryAccount(confirmCodeDto.email(), confirmCodeDto.code());
		return ApiResponse.success(account);
	}
}