package com.likelion.backendplus4.talkpick.backend.user.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.user.domain.model.User;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;
import com.likelion.backendplus4.talkpick.backend.user.presentation.controller.dto.req.UserUpdateReqDto;

/**
 * UserEntity를 User 도메인 모델로 변환하는 매퍼 클래스입니다.
 *
 * @since 2025-05-16
 */
public class UserMapper {

	/**
	 * UserEntity 객체를 User 도메인 모델로 변환합니다.
	 *
	 * @param userEntity 변환할 사용자 JPA 엔티티
	 * @return 변환된 User 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	public static User toDomainFromEntity(UserEntity userEntity) {
		return User.builder()
			.userId(userEntity.getId())
			.gender(userEntity.getGender())
			.birthday(userEntity.getBirthday())
			.name(userEntity.getName())
			.nickName(userEntity.getNickName())
			.email(userEntity.getEmail())
			.build();
	}

	/**
	 * UserUpdateReqDto를 도메인 User 객체로 변환한다.
	 *
	 * @param userId 사용자 ID
	 * @param reqDto 사용자 프로필 수정 요청 DTO
	 * @return 변환된 User 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-16
	 */
	public static User toDomainFromDto(Long userId, UserUpdateReqDto reqDto) {
		return User.builder()
			.userId(userId)
			.gender(reqDto.gender())
			.birthday(reqDto.birthDay())
			.name(reqDto.name())
			.nickName(reqDto.nickName())
			.email(reqDto.email())
			.build();
	}
}
