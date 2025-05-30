package com.likelion.backendplus4.talkpick.backend.user.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.NewsInfoDetailProviderUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.user.application.port.in.UserServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.user.application.port.out.UserManagementPort;
import com.likelion.backendplus4.talkpick.backend.user.domain.model.User;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.res.UserInfoResDto;
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
	private final NewsInfoDetailProviderUseCase newsInfoDetailProviderUseCase;

	/**
	 * 사용자 ID를 기반으로 프로필 정보를 조회합니다.
	 *
	 * @param userId 사용자 ID
	 * @return 사용자 정보를 담은 UserInfoDto
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	@Override
	@EntryExitLog
	public UserInfoResDto getMyInfo(Long userId) {
		User user = userManagementPort.getUser(userId);
		return UserInfoDtoMapper.toDtoFromUser(user);
	}

	@Override
	@EntryExitLog
	public List<NewsInfoComplete> getMyScrapHistory(Long userId) {
		return newsInfoDetailProviderUseCase.getNewsInfoDetailByUserId(userId);
	}

	/**
	 * 내 프로필 정보를 수정한다.
	 *
	 * @param user 수정할 사용자 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	@Override
	@EntryExitLog
	public void updateMyInfo(User user) {
		userManagementPort.updateUser(user);
	}

	/**
	 * 회원 정보를 논리 삭제합니다.
	 *
	 * @param id 삭제할 회원의 고유 식별자
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	@Override
	@EntryExitLog
	public void deleteUser(Long id) {
		userManagementPort.deleteUser(id);
	}
}
