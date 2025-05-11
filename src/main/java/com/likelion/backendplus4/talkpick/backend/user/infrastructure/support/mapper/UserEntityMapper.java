package com.likelion.backendplus4.talkpick.backend.user.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Role;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;

public class UserEntityMapper {

	public static UserEntity toEntityByDomain(AuthUser authUser) {
		return UserEntity.builder()
			.name(authUser.getName())
			.password(authUser.getPassword())
			.email(authUser.getEmail())
			.name(authUser.getName())
			.nickName(authUser.getNickName())
			.role(Role.USER)
			.build();
	}
}
