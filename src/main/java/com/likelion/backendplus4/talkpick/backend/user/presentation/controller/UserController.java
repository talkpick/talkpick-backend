package com.likelion.backendplus4.talkpick.backend.user.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.annotation.security.LoginUser;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;

@RestController
@RequestMapping("/user")
public class UserController {

	/**
	 * 재발급 테스트 코드입니다.
	 * TODO 삭제
	 */
	@GetMapping("/test")
	public ResponseEntity<ApiResponse<Long>> test(@LoginUser Long userId) {
		return ApiResponse.success(userId);
	}
}
