package com.likelion.backendplus4.talkpick.backend.user.application.service;

import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.user.application.port.in.UserServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.user.application.port.out.UserManagementPort;
import com.likelion.backendplus4.talkpick.backend.user.domain.model.User;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.res.UserInfoDto;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.support.mapper.UserInfoDtoMapper;

import lombok.RequiredArgsConstructor;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 클래스입니다.
 * @since 2025-05-16
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserServiceUseCase {

	private final UserManagementPort userManagementPort;

	/**
	 * 사용자 ID를 기반으로 프로필 정보를 조회합니다.
	 *
	 * @param userId 사용자 ID
	 * @return 사용자 정보를 담은 UserInfoDto
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	@Override
	public UserInfoDto getMyInfo(Long userId) {
		User user = userManagementPort.getUser(userId);
		return UserInfoDtoMapper.toDtoFromUser(user);
	}
}
