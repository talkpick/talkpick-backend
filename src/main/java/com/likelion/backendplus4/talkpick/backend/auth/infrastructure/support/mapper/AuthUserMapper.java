package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Role;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;

public class AuthUserMapper {

    public static AuthUser toDomainByUserEntity(UserEntity userEntity) {
        return AuthUser.builder()
                .userId(userEntity.getId())
                .account(userEntity.getAccount())
                .password(userEntity.getPassword())
                .role(userEntity.getRole().getRoleName())
                .name(userEntity.getName())
                .nickName(userEntity.getNickName())
                .build();
    }

    public static AuthUser toDomainByDto(SignUpDto signUpDto) {
        return AuthUser.builder()
            .account(signUpDto.account())
            .password(signUpDto.password())
            .name(signUpDto.name())
            .nickName(signUpDto.nickName())
            .email(signUpDto.email())
            .role(Role.USER.getRoleName())
            .build();
    }

}
