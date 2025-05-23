package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.persistence.adapter.out.redis;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.AuthTokenStorePort;
import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis를 이용해 리프레시 토큰 및 로그아웃 블랙리스트를 관리하는 어댑터 구현체.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 * @author 박찬병
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenStoreAdapter implements AuthTokenStorePort {

    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final String AUTHORITIES_KEY = "authorities";
    private static final String BLACKLIST = "blacklisted";
    private static final int REFRESH_TOKEN_EXPIRATION_DAYS = 7;

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * RefreshToken 과 사용자 권한 정보를 Redis에 저장합니다.
     *
     * 1. 기존 데이터 삭제
     * 2. 해시맵에 토큰 및 권한 정보 저장
     * 3. 만료 시간 설정
     *
     * @param userId       사용자 식별자
     * @param refreshToken 저장할 리프레시 토큰
     * @param roles        사용자 권한 문자열
     * @throws AuthException 저장 실패 시 발생
     * @since 2025-05-12
     * @modified 2025-05-14
     * @author 박찬병
     */
    @Override
    @EntryExitLog
    public void storeRefreshToken(String userId, String refreshToken, String roles) {
        storeRefreshTokenInternal(userId, refreshToken, roles);
    }

    /**
     * 저장된 리프레시 토큰이 유효한지 확인합니다.
     *
     * 1. Redis에서 토큰 조회
     * 2. null 검사
     * 3. 토큰 일치 여부 반환
     *
     * @param userId       사용자 식별자
     * @param refreshToken 클라이언트가 제출한 리프레시 토큰
     * @return true 면 유효, false 면 무효
     * @throws AuthException 조회 실패 시 발생
     * @since 2025-05-12
     * @modified 2025-05-14
     * @author 박찬병
     */
    @Override
    @EntryExitLog
    public boolean isValidRefreshToken(String userId, String refreshToken) {
        return isValidRefreshTokenInternal(userId, refreshToken);
    }

    /**
     * 액세스 토큰이 블랙리스트에 등록되었는지 여부를 확인합니다.
     *
     * 1. Redis 키 존재 여부 조회
     * 2. 결과 반환
     *
     * @param accessToken 검사할 액세스 토큰
     * @return true 면 블랙리스트 상태
     * @throws AuthException 조회 실패 시 발생
     * @since 2025-05-12
     * @modified 2025-05-14
     * @author 박찬병
     */
    @Override
    @EntryExitLog
    public boolean isTokenBlacklisted(String accessToken) {
        return isTokenBlacklistedInternal(accessToken);
    }

    /**
     * 로그아웃 시 액세스 토큰을 블랙리스트에 추가하고 리프레시 토큰을 삭제합니다.
     *
     * 1. 블랙리스트에 액세스 토큰 저장
     * 2. 리프레시 토큰 키 삭제
     *
     * @param accessToken           로그아웃할 액세스 토큰
     * @param accessTokenExpiration 만료까지 남은 시간(밀리초)
     * @param userId                사용자 식별자
     * @throws AuthException 처리 실패 시 발생
     * @since 2025-05-12
     * @modified 2025-05-14
     * @author 박찬병
     */
    @Override
    @EntryExitLog
    public void logoutTokens(String accessToken, long accessTokenExpiration, String userId) {
        logoutTokensInternal(accessToken, accessTokenExpiration, userId);
    }

    /**
     * Redis에서 사용자 권한 정보를 조회합니다.
     *
     * 1. 해시맵에서 AUTHORITIES_KEY 조회
     * 2. 결과 반환
     *
     * @param userId 사용자 식별자
     * @return 권한 문자열
     * @throws AuthException 조회 실패 시 발생
     * @since 2025-05-12
     * @modified 2025-05-14
     * @author 박찬병
     */
    @Override
    @EntryExitLog
    public String getAuthorities(String userId) {
        return getAuthoritiesInternal(userId);
    }


    /**
     * 1. 기존 데이터 삭제
     * 2. 해시맵에 토큰 및 권한 정보 저장
     * 3. 만료 시간 설정
     *
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-14
     */
    @EntryExitLog
    private void storeRefreshTokenInternal(String userId, String refreshToken, String roles) {
        try {
            redisTemplate.delete(userId);
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            hashOps.putAll(userId, createTokenDataMap(refreshToken, roles));
            redisTemplate.expire(userId, REFRESH_TOKEN_EXPIRATION_DAYS, TimeUnit.DAYS);
        } catch (DataAccessException dae) {
            throw new AuthException(AuthErrorCode.REDIS_STORE_FAILURE, dae);
        }
    }

    /**
     * 1. Redis에서 토큰 조회
     * 2. null 검사
     * 3. 토큰 일치 여부 반환
     *
     * @return true 면 유효, false 면 무효
     * @throws AuthException 조회 실패 시 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-14
     */
    @EntryExitLog
    private boolean isValidRefreshTokenInternal(String userId, String refreshToken) {
        try {
            String stored = Objects.requireNonNull(redisTemplate.opsForHash()
				.get(userId, REFRESH_TOKEN_KEY)).toString();
            return Objects.requireNonNull(stored).equals(refreshToken);
        } catch (DataAccessException dae) {
            throw new AuthException(AuthErrorCode.REDIS_RETRIEVE_FAILURE, dae);
        }
    }

    /**
     * 1. Redis 키 존재 여부 조회
     * 2. 결과 반환
     *
     * @return true 면 블랙리스트 상태
     * @throws AuthException 조회 실패 시 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-14
     */
    @EntryExitLog
    private boolean isTokenBlacklistedInternal(String accessToken) {
        try {
            return redisTemplate.hasKey(accessToken);
        } catch (DataAccessException dae) {
            throw new AuthException(AuthErrorCode.REDIS_BLACKLIST_CHECK_FAIL, dae);
        }
    }

    /**
     * 1. 블랙리스트에 액세스 토큰 저장
     * 2. 리프레시 토큰 키 삭제
     *
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @EntryExitLog
    private void logoutTokensInternal(String accessToken, long accessTokenExpiration, String userId) {
        try {
            redisTemplate.opsForValue().set(
                accessToken,
                BLACKLIST,
                accessTokenExpiration,
                TimeUnit.MILLISECONDS);
            redisTemplate.delete(userId);
        } catch (DataAccessException dae) {
            throw new AuthException(AuthErrorCode.REDIS_LOGOUT_PROCESS_FAIL, dae);
        }
    }

    /**
     * 1. 해시맵에서 AUTHORITIES_KEY 조회
     * 2. 결과 반환
     *
     * @return 권한 문자열
     * @throws AuthException 조회 실패 시 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-14
     */
    @EntryExitLog
    private String getAuthoritiesInternal(String userId) {
        try {
            return Objects.requireNonNull(redisTemplate.opsForHash()
				.get(userId, AUTHORITIES_KEY)).toString();
        } catch (DataAccessException dae) {
            throw new AuthException(AuthErrorCode.REDIS_AUTHORITIES_RETRIEVE_FAIL, dae);
        }
    }

    /**
     * 1. REFRESH_TOKEN_KEY 매핑
     * 2. AUTHORITIES_KEY 매핑
     *
     * @return 토큰 데이터 맵
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-14
     */
    @EntryExitLog
    private HashMap<String, String> createTokenDataMap(String refreshToken, String authorities) {
        HashMap<String, String> map = new HashMap<>();
        map.put(REFRESH_TOKEN_KEY, refreshToken);
        map.put(AUTHORITIES_KEY, authorities);
        return map;
    }

}