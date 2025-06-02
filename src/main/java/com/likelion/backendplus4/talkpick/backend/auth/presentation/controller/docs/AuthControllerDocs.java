package com.likelion.backendplus4.talkpick.backend.auth.presentation.controller.docs;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.check.CheckAccountDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.check.CheckEmailDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.check.CheckNicknameDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.confirm.ConfirmCodeDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.RefreshReqDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.SignInDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.recovery.RecoveryPasswordDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.recovery.RecoveryUserInfoDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.res.TokenResDto;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(
	name = "Auth",
	description = "회원 가입, 로그인, 로그아웃, 토큰 재발급 등 인증 관련 API"
)
@RequestMapping("/auth")
public interface AuthControllerDocs {

	@Operation(
		summary = "회원 가입",
		description = "회원 가입 정보(SignUpDto)를 받아 새로운 사용자를 등록합니다."
	)
	ResponseEntity<ApiResponse<Void>> signUp(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "회원 가입 요청 정보",
			required = true
		)
		@Valid @RequestBody SignUpDto signUpDto
	);

	@Operation(
		summary = "로그인",
		description = "계정과 비밀번호(SignInDto)를 받아 인증 후 Access/Refresh 토큰을 발급합니다."
	)
	ResponseEntity<ApiResponse<TokenResDto>> signIn(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "로그인 요청 정보",
			required = true
		)
		@Valid @RequestBody SignInDto signInDto
	);

	@Operation(
		summary = "로그아웃",
		description = "Authorization 헤더에서 Access Token을 추출하여 블랙리스트에 등록하고 Refresh Token을 삭제합니다."
	)
	ResponseEntity<ApiResponse<Void>> logout(
		@Parameter(
			in = ParameterIn.HEADER,
			description = "Authorization: Bearer {accessToken}",
			required = true
		)
		HttpServletRequest request
	);

	@Operation(
		summary = "Access Token 재발급",
		description = "클라이언트가 제출한 Refresh Token(RefreshReqDto)을 검증하여 새로운 Access Token을 반환합니다."
	)
	ResponseEntity<ApiResponse<TokenResDto>> refreshAccessToken(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "Refresh Token 요청 정보",
			required = true
		)
		@Valid @RequestBody RefreshReqDto requestToken
	);

	@Operation(
		summary = "계정 중복 검사",
		description = "클라이언트가 제출한 account(CheckAccountDto)에 대해 중복 여부를 확인합니다."
	)
	ResponseEntity<ApiResponse<Void>> checkDuplicateAccount(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "검사할 계정 정보",
			required = true
		)
		@Valid @RequestBody CheckAccountDto checkAccountDto
	);

	@Operation(
		summary = "이메일 중복 검사 및 인증 코드 전송",
		description = "클라이언트가 제출한 이메일(CheckEmailDto)에 대해 중복 여부를 확인하고, 인증 코드를 발송합니다."
	)
	ResponseEntity<ApiResponse<Void>> verifyEmailDuplicationAndSendCode(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "검사할 이메일 정보",
			required = true
		)
		@Valid @RequestBody CheckEmailDto checkEmailDto
	);

	@Operation(
		summary = "닉네임 중복 검사",
		description = "클라이언트가 제출한 닉네임(CheckNicknameDto)에 대해 중복 여부를 확인합니다."
	)
	ResponseEntity<ApiResponse<Void>> checkDuplicateNickname(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "검사할 닉네임 정보",
			required = true
		)
		@Valid @RequestBody CheckNicknameDto checkNicknameDto
	);

	@Operation(
		summary = "이메일 인증 코드 검증",
		description = "클라이언트가 제출한 이메일과 인증 코드(ConfirmCodeDto)를 검증합니다."
	)
	ResponseEntity<ApiResponse<Void>> verifyEmailCode(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "이메일 및 인증 코드 정보",
			required = true
		)
		@Valid @RequestBody ConfirmCodeDto confirmDto
	);

	@Operation(
		summary = "계정 복구용 인증 코드 발송",
		description = "사용자 이름과 이메일(RecoveryUserInfoDto)을 받아 계정 복구용 인증 코드를 이메일로 발송합니다."
	)
	ResponseEntity<ApiResponse<Void>> sendAccountRecoveryCode(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "계정 복구용 사용자 정보",
			required = true
		)
		@Valid @RequestBody RecoveryUserInfoDto recoveryUserInfoDto
	);

	@Operation(
		summary = "계정 복구 결과",
		description = "이메일과 인증 코드(ConfirmCodeDto)를 검증하여 사용자의 계정을 반환합니다."
	)
	ResponseEntity<ApiResponse<String>> recoveryAccount(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "이메일 및 인증 코드 정보",
			required = true
		)
		@Valid @RequestBody ConfirmCodeDto confirmCodeDto
	);

	@Operation(
		summary = "비밀번호 복구용 인증 코드 발송",
		description = "사용자 이름, 이메일, 계정 정보(RecoveryUserInfoDto)를 검증하여 비밀번호 복구용 인증 코드를 이메일로 발송합니다."
	)
	ResponseEntity<ApiResponse<Void>> sendPasswordRecoveryCode(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "비밀번호 복구용 사용자 정보",
			required = true
		)
		@Valid @RequestBody RecoveryUserInfoDto recoveryUserInfoDto
	);

	@Operation(
		summary = "비밀번호 복구 코드 검증 및 임시 토큰 발급",
		description = "이메일과 인증 코드(ConfirmCodeDto)를 검증하여 임시 토큰을 발급합니다."
	)
	ResponseEntity<ApiResponse<String>> verifyPasswordRecoveryCode(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "이메일 및 인증 코드 정보",
			required = true
		)
		@Valid @RequestBody ConfirmCodeDto confirmCodeDto
	);

	@Operation(
		summary = "비밀번호 재설정",
		description = "임시 토큰과 이메일(RecoveryPasswordDto)을 검증하고, 새로운 비밀번호를 암호화하여 저장합니다."
	)
	ResponseEntity<ApiResponse<Void>> recoveryPassword(
		@Parameter(
			in = ParameterIn.DEFAULT,
			description = "이메일, 임시 토큰 및 새 비밀번호 정보",
			required = true
		)
		@Valid @RequestBody RecoveryPasswordDto recoveryPasswordDto
	);
}