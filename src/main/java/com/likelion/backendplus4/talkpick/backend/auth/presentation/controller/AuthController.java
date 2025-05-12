package com.likelion.backendplus4.talkpick.backend.auth.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.in.AuthServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.AuthUserMapper;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignInDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.TokenDto;
import com.likelion.backendplus4.talkpick.backend.common.annotation.security.LoginUser;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;

import lombok.RequiredArgsConstructor;

/**
 * 인증 관련 API 요청을 처리하는 컨트롤러.
 *
 * @author 박찬병
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
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
	@PostMapping("/signUp")
	public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody SignUpDto signUpDto) {
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
	@PostMapping("/signIn")
	public ResponseEntity<ApiResponse<TokenDto>> signIn(@RequestBody SignInDto signInDto) {
		TokenDto tokenDto = authServiceUseCase.signIn(signInDto.account(), signInDto.password());
		return ApiResponse.success(tokenDto);
	}

	/**
	 * 로그아웃 요청을 처리합니다.
	 *
	 * @param tokenDto 로그아웃할 토큰 정보 DTO
	 * @return 빈 응답(ApiResponse<Void>)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(@RequestBody TokenDto tokenDto) {
		authServiceUseCase.logout(tokenDto.accessToken());
		return ApiResponse.success();
	}

	/**
	 * 회원 탈퇴 요청을 처리합니다.
	 *
	 * @param memberId 탈퇴할 회원의 ID
	 * @return 빈 응답(ApiResponse<Void>)
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse<Void>> deleteAccount(@LoginUser Long memberId) {
		authServiceUseCase.deleteUser(memberId);
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
	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<TokenDto>> refreshAccessToken(
		@RequestBody TokenDto requestToken
	) {
		TokenDto tokenDto = authServiceUseCase.refreshToken(requestToken.refreshToken());
		return ApiResponse.success(tokenDto);
	}

}