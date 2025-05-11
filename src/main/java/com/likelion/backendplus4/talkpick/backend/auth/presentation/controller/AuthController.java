package com.likelion.backendplus4.talkpick.backend.auth.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.in.AuthServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.dto.JwtToken;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignInDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.common.annotation.security.LoginUser;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthServiceUseCase authServiceUseCase;

	@PostMapping("/signUp")
	public ResponseEntity<Void> signUp(@RequestBody SignUpDto signUpDto) {
		authServiceUseCase.signUp(signUpDto);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/signIn")
	public ResponseEntity<JwtToken> signIn(@RequestBody SignInDto signInDto) {
		JwtToken jwtToken = authServiceUseCase.signIn(signInDto);
		return ResponseEntity.ok(jwtToken);
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(@RequestBody JwtToken jwtToken) {
		authServiceUseCase.logout(jwtToken.accessToken());
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/delete")
	public ResponseEntity<Void> deleteAccount(@LoginUser Long memberId) {
		authServiceUseCase.deleteUser(memberId);
		return ResponseEntity.ok().build();
	}

	@PostMapping("/refresh")
	public ResponseEntity<JwtToken> refreshAccessToken(
		@RequestBody JwtToken jwtToken
	) {
		JwtToken newAccessToken = authServiceUseCase.refreshToken(jwtToken.refreshToken());
		return ResponseEntity.ok(newAccessToken);
	}

}
