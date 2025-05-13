package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Role;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;

/**
 * UserEntity 또는 SignUpDto를 AuthUser 도메인 모델로 매핑하는 유틸리티 클래스.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public class AuthUserMapper {

    /**
     * UserEntity로부터 AuthUser 도메인 객체를 생성합니다.
     *
     * @param userEntity 영속화된 UserEntity
     * @return AuthUser 도메인 모델
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    public static AuthUser toDomainByUserEntity(UserEntity userEntity) {
        return AuthUser.builder()
            .userId(userEntity.getId())
            .account(userEntity.getAccount())
            .password(userEntity.getPassword())
            .role(userEntity.getRole().getRoleName())
            .gender(userEntity.getGender())
            .birthDay(userEntity.getBirthday())
            .name(userEntity.getName())
            .nickName(userEntity.getNickName())
            .build();
    }

    /**
     * SignUpDto로부터 AuthUser 도메인 객체를 생성합니다.
     *
     * @param signUpDto 회원 가입 요청 DTO
     * @return AuthUser 도메인 모델
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    public static AuthUser toDomainByDto(SignUpDto signUpDto) {
        return AuthUser.builder()
            .account(signUpDto.account())
            .password(signUpDto.password())
            .name(signUpDto.name())
            .nickName(signUpDto.nickName())
            .email(signUpDto.email())
            .gender(signUpDto.gender())
            .birthDay(signUpDto.birthDay())
            .role(Role.USER.getRoleName())
            .build();
    }
}