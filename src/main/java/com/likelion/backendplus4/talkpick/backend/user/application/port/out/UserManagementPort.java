package com.likelion.backendplus4.talkpick.backend.user.application.port.out;

import com.likelion.backendplus4.talkpick.backend.user.domain.model.User;
import com.likelion.backendplus4.talkpick.backend.user.exception.UserException;

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

	/**
	 * 전달받은 사용자 정보를 기반으로 사용자 엔터티를 업데이트합니다.
	 *
	 * @param user 업데이트할 사용자 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	void updateUser(User user);

	/**
	 * 사용자 ID로 회원을 삭제합니다.
	 *
	 * 1. ID로 Entity 조회
	 * 2. 존재하지 않으면 예외 발생
	 * 3. Repository를 통해 삭제
	 *
	 * @param id 삭제할 사용자 고유 식별자
	 * @throws UserException 사용자가 존재하지 않을 경우
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	void deleteUser(Long id);
}
