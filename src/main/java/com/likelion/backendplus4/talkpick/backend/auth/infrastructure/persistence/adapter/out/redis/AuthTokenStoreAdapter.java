package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.persistence.adapter.out.redis;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.AuthTokenStorePort;
import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;
import com.likelion.backendplus4.talkpick.backend.common.annotation.logging.EntryExitLog;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
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

    @Value("${auth.redis.refresh-token-key}")
    private String refreshTokenKey;

    @Value("${auth.redis.authorities-key}")
    private String authoritiesKey;

    @Value("${auth.redis.blacklist-value}")
    private String blacklistValue;

    @Value("${auth.redis.email-prefix}")
    private String emailKey;

    @Value("${auth.redis.verify-email-code-ttl}")
    private Duration verifyEmailCodeTtl;

    @Value("${auth.redis.refresh-token-expiration-days}")
    private int refreshTokenExpirationDays;

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
     * 이메일 인증 코드를 Redis에 저장합니다.
     * 만약 계정 찾기를 하는 경우 계정 정보도 같이 저장합니다.
     *
     * @param email 인증 코드를 저장할 이메일 주소
     * @param emailAuthCode 저장할 인증 코드
     * @param account (Nullable) 계정
     * @author 박찬병
     * @since 2025-05-20
     */
    @Override
    @EntryExitLog
    public void saveEmailAuthData(String email, String emailAuthCode, String account) {
        String key = emailKey + email;
        Map<String,String> map = new HashMap<>();
        map.put("emailAuthCode", emailAuthCode);
        if (account != null) map.put("account", account);
        redisTemplate.opsForHash().putAll(key, map);
        redisTemplate.expire(key, verifyEmailCodeTtl);
    }

    /**
     * 이메일 인증 코드를 검증합니다.
     *
     * Redis에서 저장된 인증 코드가 존재하는지 확인하고,
     * 사용자가 입력한 코드와 일치하는지 검증합니다.
     * 검증에 성공하면 해당 인증 코드를 Redis에서 삭제합니다.
     *
     * @param email 인증할 이메일 주소
     * @param inputCode 사용자가 입력한 인증 코드
     * @throws AuthException 인증 실패 시 예외 발생
     * @author 박찬병
     * @since 2025-05-20
     */
    @Override
    @EntryExitLog
    public Optional<String> verifyCode(String email, String inputCode) {
        String key = emailKey + email;
        String saved = (String)redisTemplate.opsForHash()
            .get(key, "emailAuthCode");

        checkSaveCodeNull(saved);
        checkSaveCodeNotEqual(inputCode, saved);

        String account = (String)redisTemplate.opsForHash()
            .get(key, "account");

        redisTemplate.delete(key);
        return Optional.ofNullable(account);
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
            redisTemplate.expire(userId, refreshTokenExpirationDays, TimeUnit.DAYS);
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
				.get(userId, refreshTokenKey)).toString();
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
     * 저장된 인증 코드가 존재하지 않는 경우 예외를 발생시킵니다.
     *
     * @param savedCode Redis에 저장된 인증 코드
     * @throws AuthException 인증 코드가 존재하지 않을 경우
     * @author 박찬병
     * @since 2025-05-20
     */
    private void checkSaveCodeNull(String savedCode) {
        if (savedCode == null) {
            throw new AuthException(AuthErrorCode.VERIFY_CODE_NOT_FOUND);
        }
    }

    /**
     * 저장된 인증 코드가 입력값과 일치하지 않는 경우 예외를 발생시킵니다.
     *
     * @param code 사용자가 입력한 인증 코드
     * @param savedCode Redis에 저장된 인증 코드
     * @throws AuthException 인증 코드가 일치하지 않을 경우
     * @author 박찬병
     * @since 2025-05-20
     */
    private void checkSaveCodeNotEqual(String code, String savedCode) {
        if (!savedCode.equals(code)) {
            throw new AuthException(AuthErrorCode.VERIFY_CODE_MISMATCH);
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
                blacklistValue,
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
				.get(userId, authoritiesKey)).toString();
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
        map.put(refreshTokenKey, refreshToken);
        map.put(authoritiesKey, authorities);
        return map;
    }

}