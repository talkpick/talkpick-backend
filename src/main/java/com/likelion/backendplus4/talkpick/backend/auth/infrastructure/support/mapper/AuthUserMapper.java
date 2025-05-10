package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.entity.UserEntity;

public class AuthUserMapper {

    public static AuthUser toDomain(UserEntity userEntity) {
        return AuthUser.builder()
                .userId(userEntity.getId())
                .account(userEntity.getAccount())
                .password(userEntity.getPassword())
                .role(userEntity.getRole().getRoleName())
                .name(userEntity.getName())
                .nickName(userEntity.getNickName())
                .build();
    }

}
