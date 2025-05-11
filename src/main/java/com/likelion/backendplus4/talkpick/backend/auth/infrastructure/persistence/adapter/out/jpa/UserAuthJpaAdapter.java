package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.persistence.adapter.out.jpa;

import java.util.Optional;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.AuthUserMapper;
import com.likelion.backendplus4.talkpick.backend.user.exception.UserException;
import com.likelion.backendplus4.talkpick.backend.user.exception.error.UserErrorCode;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.UserRepository;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.support.mapper.UserEntityMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthJpaAdapter implements UserAuthPort {

    private final UserRepository userRepository;

    @Override
    public Optional<AuthUser> findUserById(Long id) {
        return userRepository.findById(id)
            .map(AuthUserMapper::toDomainByUserEntity);
    }

    @Override
    public void existsByAccountAndEmail(String account) {
        if(userRepository.existsByAccount(account)){
            throw new UserException(UserErrorCode.ACCOUNT_DUPLICATE);
        }
    }

    @Override
    public void saveUser(AuthUser authUser) {
        UserEntity userEntity = UserEntityMapper.toEntityByDomain(authUser);
        userRepository.save(userEntity);
    }

    @Override
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(
            () -> new UserException(UserErrorCode.USER_NOT_FOUND)
        );

        // TODO 논리 삭제 처리
        userRepository.delete(userEntity);
    }
}
