package com.likelion.backendplus4.talkpick.backend.auth.application.port.out;

import java.util.Optional;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.user.exception.UserException;

/**
 * AuthUser 관련 CRUD 작업을 처리하는 포트.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public interface UserRepositoryPort {

    /**
     * 계정으로 사용자 정보를 조회합니다.
     *
     * @param account 조회할 사용자 계정
     * @return Optional<AuthUser> 조회된 도메인 모델(Optional)
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    Optional<AuthUser> findUserByAccount(String account);

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
    void existsByAccount(String account);

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
    void existsByEmail(String email);

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
    void existsByNickname(String nickname);

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
    void saveUser(AuthUser authUser);

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
	String findUserAccountByNameAndEmail(String name, String email);

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
    void validateUserExistence(String name, String email, String account);

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
    void updateUserPassword(String email, String encodePassword);
}
