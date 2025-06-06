package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.user;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserRepositoryPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.CustomUserDetailsMapper;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Spring Security가 사용자 인증 정보를 조회할 때 사용하는 커스텀 UserDetailsService 구현체.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepositoryPort userRepositoryPort;

    /**
     * 계정으로 회원 정보를 조회하고 UserDetails로 변환합니다.
     *
     * 1. UserJpaRepoPort를 통해 AuthUser 조회
     * 2. 조회된 AuthUser가 없으면 AuthException 발생
     * 3. CustomUserDetails로 매핑하여 반환
     *
     * @param account 로그인 시도할 사용자 계정
     * @return UserDetails 인증에 사용할 사용자 상세 정보
     * @throws AuthException 계정이 존재하지 않거나 조회 실패 시 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Override
    @EntryExitLog
    public UserDetails loadUserByUsername(String account) {
        AuthUser authUser = getAuthUserByAccount(account);
        return CustomUserDetailsMapper.toCustomUserDetails(authUser);
    }

    /**
     * 계정으로 사용자 정보를 조회하여 AuthUser 객체를 반환합니다.
     *
     * 1. UserRepositoryPort를 통해 AuthUser를 조회
     * 2. 조회된 AuthUser가 없으면 AuthException 발생
     *
     * @param account 조회할 사용자 계정
     * @return 조회된 AuthUser 객체
     * @throws AuthException 계정이 존재하지 않거나 조회 실패 시 발생
     * @since 2025-05-14
     * @modified 2025-05-14
     * @author 박찬병
     */
    @EntryExitLog
    private AuthUser getAuthUserByAccount(String account) {
        return userRepositoryPort.findUserByAccount(account)
            .orElseThrow(() -> new AuthException(AuthErrorCode.AUTHENTICATION_FAILED));
    }

}