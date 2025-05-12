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

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthServiceUseCase authServiceUseCase;

	@PostMapping("/signUp")
	public ResponseEntity<ApiResponse<Void>> signUp(@RequestBody SignUpDto signUpDto) {
		AuthUser user = AuthUserMapper.toDomainByDto(signUpDto);
		authServiceUseCase.signUp(user);
		return ApiResponse.success();
	}

	@PostMapping("/signIn")
	public ResponseEntity<ApiResponse<TokenDto>> signIn(@RequestBody SignInDto signInDto) {
		TokenDto tokenDto = authServiceUseCase.signIn(signInDto.account(), signInDto.password());
		return ApiResponse.success(tokenDto);
	}

	@PostMapping("/logout")
	public ResponseEntity<ApiResponse<Void>> logout(@RequestBody TokenDto tokenDto) {
		authServiceUseCase.logout(tokenDto.accessToken());
		return ApiResponse.success();
	}

	@DeleteMapping("/delete")
	public ResponseEntity<ApiResponse<Void>> deleteAccount(@LoginUser Long memberId) {
		authServiceUseCase.deleteUser(memberId);
		return ApiResponse.success();
	}

	@PostMapping("/refresh")
	public ResponseEntity<ApiResponse<TokenDto>> refreshAccessToken(
		@RequestBody TokenDto requestToken
	) {
		TokenDto tokenDto = authServiceUseCase.refreshToken(requestToken.refreshToken());
		return ApiResponse.success(tokenDto);
	}

}
