package com.likelion.backendplus4.talkpick.backend.user.presentation.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.annotation.security.LoginUser;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.user.application.port.in.UserServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.res.UserInfoDto;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 관련 요청을 처리하는 컨트롤러입니다.
 *
 * @since 2025-05-16
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

	private final UserServiceUseCase userServiceUseCase;


	/**
	 * 로그인한 사용자의 프로필 정보를 조회합니다.
	 *
	 * @param userId 로그인된 사용자 ID
	 * @return 사용자 프로필 정보를 담은 응답 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	@GetMapping("/profile")
	public ResponseEntity<ApiResponse<UserInfoDto>> getProfile(@LoginUser Long userId) {
		UserInfoDto myInfo = userServiceUseCase.getMyInfo(userId);
		return ApiResponse.success(myInfo);
	}
}
