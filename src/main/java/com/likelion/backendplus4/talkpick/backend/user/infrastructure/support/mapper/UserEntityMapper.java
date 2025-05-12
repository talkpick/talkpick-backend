package com.likelion.backendplus4.talkpick.backend.user.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Role;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;

/**
 * UserEntity로 변환하는 매퍼 클래스입니다.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public class UserEntityMapper {

	/**
	 * AuthUser의 필드를 UserEntity로 매핑합니다.
	 *
	 * @param authUser 매핑할 AuthUser 도메인 객체
	 * @return UserEntity 생성된 엔티티 객체
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-12
	 */
	public static UserEntity toEntityByDomain(AuthUser authUser) {
		return UserEntity.builder()
			.account(authUser.getAccount())
			.password(authUser.getPassword())
			.email(authUser.getEmail())
			.name(authUser.getName())
			.nickName(authUser.getNickName())
			.role(Role.USER)
			.build();
	}
}