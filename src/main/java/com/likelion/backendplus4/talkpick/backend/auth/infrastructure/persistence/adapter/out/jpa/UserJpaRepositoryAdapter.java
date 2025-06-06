package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.persistence.adapter.out.jpa;

import java.util.Optional;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserRepositoryPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.AuthUserMapper;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;
import com.likelion.backendplus4.talkpick.backend.user.exception.UserException;
import com.likelion.backendplus4.talkpick.backend.user.exception.error.UserErrorCode;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.repository.UserRepository;
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
public class UserJpaRepositoryAdapter implements UserRepositoryPort {

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
    @EntryExitLog
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
    @EntryExitLog
    public void existsByAccount(String account) {
        checkAccountDuplicate(account);
    }

    /**
     * 이메일 중복 여부를 검사하고, 중복인 경우 예외를 발생시킵니다.
     *
     * 1. 이메일 존재 여부 조회
     * 2. 중복 시 UserException 발생
     *
     * @param email 검사할 계정
     * @throws UserException 중복된 계정인 경우
     * @author 박찬병
     * @since 2025-05-15
     * @modified 2025-05-15
     */
    @Override
    @EntryExitLog
    public void existsByEmail(String email) {
        checkEmailDuplicate(email);
    }

    /**
     * 닉네임 중복 여부를 검사하고, 중복인 경우 예외를 발생시킵니다.
     *
     * 1. 닉네임 존재 여부 조회
     * 2. 중복 시 UserException 발생
     *
     * @param nickname 검사할 닉네임
     * @throws UserException 중복된 닉네임 경우
     * @author 박찬병
     * @since 2025-05-15
     * @modified 2025-05-15
     */
    @Override
    @EntryExitLog
    public void existsByNickname(String nickname) {
        checkNicknameDuplicate(nickname);
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
    @EntryExitLog
    public void saveUser(AuthUser authUser) {
        UserEntity userEntity = UserEntityMapper.toEntityByDomain(authUser);
        userRepository.save(userEntity);
    }

    /**
     * 이름과 이메일을 기반으로 사용자를 조회하고, 해당 사용자의 계정 아이디를 반환합니다.
     *
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @return 조회된 사용자의 계정 아이디
     * @throws UserException 사용자가 존재하지 않을 경우 예외 발생
     * @author 박찬병
     * @since 2025-05-20
     */
    @Override
    @EntryExitLog
    public String findUserAccountByNameAndEmail(String name, String email) {
        UserEntity userEntity = findUserByNameAndEmail(name, email);
        return userEntity.getAccount();
    }

    /**
     * 주어진 이름, 이메일, 계정을 기반으로 사용자의 존재 여부를 확인합니다.
     *
     * 해당 조건에 부합하는 사용자가 존재하지 않을 경우 예외가 발생합니다.
     *
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @param account 사용자 계정 ID
     * @throws UserException 사용자가 존재하지 않을 경우
     * @author 박찬병
     * @since 2025-05-20
     */
    @Override
    @EntryExitLog
    public void validateUserExistence(String name, String email, String account) {
        findUserByNameEmailAndAccount(name, email, account);
    }


    /**
     * 이메일을 기준으로 사용자를 찾아 비밀번호를 업데이트합니다.
     *
     * 인코딩된 새 비밀번호로 사용자 정보를 갱신합니다.
     *
     * @param email 사용자 이메일
     * @param encodePassword 인코딩된 새 비밀번호
     * @throws UserException 해당 이메일로 사용자를 찾을 수 없는 경우
     * @author 박찬병
     * @since 2025-05-20
     */
    @Override
    @Transactional
    @EntryExitLog
    public void updateUserPassword(String email, String encodePassword) {
        UserEntity findUser = findUserByEmail(email);
        findUser.updatePassword(encodePassword);
    }

    /**
     * 계정 중복을 확인하고, 중복 시 예외를 던집니다.
     *
     * 1. 계정 존재 여부 조회
     * 2. 중복 시 UserException 발생
     *
     * @param account 검사할 계정
     * @throws UserException 중복된 계정인 경우 발생
     * @since 2025-05-12
     * @modified 2025-05-12
     * @author 박찬병
     */
    @EntryExitLog
    private void checkAccountDuplicate(String account) {
        if (userRepository.existsByAccount(account)) {
            throw new UserException(UserErrorCode.ACCOUNT_DUPLICATE);
        }
    }

    /**
     * 이메일 중복을 확인하고, 중복 시 예외를 던집니다.
     *
     * 1. 이메일 존재 여부 조회
     * 2. 중복 시 UserException 발생
     *
     * @param email 검사할 이메일
     * @throws UserException 중복된 이메일인 경우 발생
     * @since 2025-05-15
     * @modified 2025-05-15
     * @author 박찬병
     */
    @EntryExitLog
    private void checkEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new UserException(UserErrorCode.EMAIL_DUPLICATE);
        }
    }


    /**
     * 닉네임 중복을 확인하고, 중복 시 예외를 던집니다.
     *
     * 1. 닉네임 존재 여부 조회
     * 2. 중복 시 UserException 발생
     *
     * @param nickname 검사할 닉네임
     * @throws UserException 중복된 이메일인 경우 발생
     * @since 2025-05-15
     * @modified 2025-05-15
     * @author 박찬병
     */
    @EntryExitLog
    private void checkNicknameDuplicate(String nickname) {
        if (userRepository.existsByNickName(nickname)) {
            throw new UserException(UserErrorCode.NICKNAME_DUPLICATE);
        }
    }

    /**
     * 이름과 이메일을 기준으로 사용자를 조회합니다.
     *
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @return 조회된 사용자 엔터티
     * @throws UserException 사용자가 존재하지 않을 경우 예외 발생
     * @author 박찬병
     * @since 2025-05-20
     */
    @EntryExitLog
    private UserEntity findUserByNameAndEmail(String name, String email) {
        return userRepository.findUserByNameAndEmail(name, email)
            .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    /**
     * 이름, 이메일, 계정을 기준으로 사용자의 존재 여부를 확인합니다.
     *
     * 사용자 조회에 실패할 경우 예외가 발생합니다.
     *
     * @param name 사용자 이름
     * @param email 사용자 이메일
     * @param account 사용자 계정 ID
     * @throws UserException 사용자가 존재하지 않을 경우 예외 발생
     * @author 박찬병
     * @since 2025-05-20
     */
    @EntryExitLog
    private void findUserByNameEmailAndAccount(String name, String email, String account) {
        userRepository.findUserByNameAndEmailAndAccount(name, email,
            account).orElseThrow(
            () -> new UserException(UserErrorCode.USER_NOT_FOUND)
        );
    }

    /**
     * 이메일을 기준으로 사용자를 조회합니다.
     *
     * @param email 사용자 이메일
     * @return 조회된 사용자 엔터티
     * @throws UserException 사용자가 존재하지 않을 경우 예외 발생
     * @author 박찬병
     * @since 2025-05-20
     */
    @EntryExitLog
    private UserEntity findUserByEmail(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(
            () -> new UserException(UserErrorCode.USER_NOT_FOUND)
        );
    }

}