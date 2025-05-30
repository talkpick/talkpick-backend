package com.likelion.backendplus4.talkpick.backend.user.application.port.in;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.news.info.domain.model.NewsInfoComplete;
import com.likelion.backendplus4.talkpick.backend.user.domain.model.User;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.res.UserInfoResDto;

/**
 * 사용자 관련 비즈니스 로직을 처리하는 서비스 유스케이스 입니다.
 * @since 2025-05-16
 */
public interface UserServiceUseCase {

	/**
	 * 사용자 ID를 기반으로 프로필 정보를 조회합니다.
	 *
	 * @param userId 사용자 ID
	 * @return 사용자 정보를 담은 UserInfoDto
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	UserInfoResDto getMyInfo(Long userId);

	List<NewsInfoComplete> getMyScrapHistory(Long userId);

	/**
	 * 내 프로필 정보를 수정한다.
	 *
	 * @param user 수정할 사용자 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	void updateMyInfo(User user);

	/**
	 * 회원 정보를 영구 삭제합니다.
	 *
	 * @param id 삭제할 회원의 고유 식별자
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	void deleteUser(Long id);
}
