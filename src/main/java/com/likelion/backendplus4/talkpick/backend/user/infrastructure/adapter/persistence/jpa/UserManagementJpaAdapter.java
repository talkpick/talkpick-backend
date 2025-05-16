package com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.user.application.port.out.UserManagementPort;
import com.likelion.backendplus4.talkpick.backend.user.domain.model.User;
import com.likelion.backendplus4.talkpick.backend.user.exception.UserException;
import com.likelion.backendplus4.talkpick.backend.user.exception.error.UserErrorCode;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.repository.UserRepository;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.support.mapper.UserMapper;

import lombok.RequiredArgsConstructor;


/**
 * JPA를 이용해 사용자 정보를 조회하는 영속성 어댑터입니다.
 *
 * @since 2025-05-16
 */
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserManagementJpaAdapter implements UserManagementPort {

	private final UserRepository userRepository;

	/**
	 * 사용자 ID를 기반으로 사용자 엔터티를 조회합니다.
	 *
	 * @param userId 조회할 사용자 ID
	 * @return 조회된 사용자 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	@Override
	@EntryExitLog
	public User getUser(Long userId) {
		UserEntity userEntity = fetchUserOrThrow(userId);
		return UserMapper.toDomainFromEntity(userEntity);
	}

	/**
	 * 전달받은 User 도메인 객체의 정보로 사용자 정보를 업데이트한다.
	 *
	 * @param user 업데이트할 사용자 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	@Override
	@Transactional
	@EntryExitLog
	public void updateUser(User user) {
		UserEntity userEntity = fetchUserOrThrow(user.getUserId());
		userEntity.updateUser(user);
	}

	/**
	 * ID로 사용자 엔티티를 조회하고, 존재하지 않으면 예외를 던집니다.
	 *
	 * 1. ID로 Entity 조회
	 * 2. 없으면 UserException 발생
	 *
	 * @param id 조회할 사용자 ID
	 * @return 존재하는 UserEntity
	 * @throws UserException 사용자가 존재하지 않을 경우
	 * @since 2025-05-16
	 * @modified 2025-05-16
	 * @author 박찬병
	 */
	private UserEntity fetchUserOrThrow(Long id) {
		return userRepository.findById(id)
			.orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
	}
}
