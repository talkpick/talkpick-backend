package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.persistence.adapter.out.jpa;

import java.util.Optional;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserJpaRepoPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.AuthUserMapper;
import com.likelion.backendplus4.talkpick.backend.user.exception.UserException;
import com.likelion.backendplus4.talkpick.backend.user.exception.error.UserErrorCode;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.UserRepository;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.support.mapper.UserEntityMapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserJpaRepoJpaAdapter implements UserJpaRepoPort {

    private final UserRepository userRepository;

    @Override
    public Optional<AuthUser> findUserByAccount(String account) {
        return userRepository.findUserByAccount(account)
            .map(AuthUserMapper::toDomainByUserEntity);
    }

    @Override
    public void existsByAccountAndEmail(String account) {
        if(userRepository.existsByAccount(account)){
            throw new UserException(UserErrorCode.ACCOUNT_DUPLICATE);
        }
    }

    @Override
    @Transactional
    public void saveUser(AuthUser authUser) {
        UserEntity userEntity = UserEntityMapper.toEntityByDomain(authUser);
        userRepository.save(userEntity);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow(
            () -> new UserException(UserErrorCode.USER_NOT_FOUND)
        );

        // TODO 논리 삭제 처리
        userRepository.delete(userEntity);
    }
}
