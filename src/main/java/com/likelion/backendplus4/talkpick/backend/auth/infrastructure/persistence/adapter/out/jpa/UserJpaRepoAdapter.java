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

/**
 * JPA를 통해 AuthUser 관련 CRUD 작업을 처리하는 어댑터 구현체.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserJpaRepoAdapter implements UserJpaRepoPort {

    private final UserRepository userRepository;

    /**
     * 계정으로 사용자 정보를 조회합니다.
     *
     * @param account 조회할 사용자 계정
     * @return Optional<AuthUser> 조회된 도메인 모델(Optional)
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Override
    public Optional<AuthUser> findUserByAccount(String account) {
        return userRepository.findUserByAccount(account)
            .map(AuthUserMapper::toDomainByUserEntity);
    }

    /**
     * 계정 중복 여부를 검사하고, 중복인 경우 예외를 발생시킵니다.
     *
     * 1. 계정 존재 여부 조회
     * 2. 중복 시 UserException 발생
     *
     * @param account 검사할 계정
     * @throws UserException 중복된 계정인 경우
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Override
    public void existsByAccountAndEmail(String account) {
        if (userRepository.existsByAccount(account)) {
            throw new UserException(UserErrorCode.ACCOUNT_DUPLICATE);
        }
    }

    /**
     * 새로운 AuthUser 도메인 객체를 저장합니다.
     *
     * 1. 도메인 모델을 Entity로 변환
     * 2. Repository를 통해 저장
     *
     * @param authUser 저장할 도메인 모델
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Override
    @Transactional
    public void saveUser(AuthUser authUser) {
        UserEntity userEntity = UserEntityMapper.toEntityByDomain(authUser);
        userRepository.save(userEntity);
    }

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
    @Override
    @Transactional
    public void deleteUser(Long id) {
        UserEntity userEntity = userRepository.findById(id)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        // TODO 논리 삭제 처리
        userRepository.delete(userEntity);
    }
}