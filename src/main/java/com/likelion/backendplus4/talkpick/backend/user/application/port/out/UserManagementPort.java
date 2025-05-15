package com.likelion.backendplus4.talkpick.backend.user.application.port.out;

import com.likelion.backendplus4.talkpick.backend.user.domain.model.User;

/**
 * JPA를 이용해 사용자 정보를 조회하는 영속성 어댑터입니다.
 *
 * @since 2025-05-16
 */
public interface UserManagementPort {

	/**
	 * 사용자 ID를 기반으로 사용자 엔터티를 조회합니다.
	 *
	 * @param userId 조회할 사용자 ID
	 * @return 조회된 사용자 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	User getUser(Long userId);
}
