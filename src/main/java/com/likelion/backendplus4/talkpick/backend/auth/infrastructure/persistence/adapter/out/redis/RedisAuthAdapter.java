package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.persistence.adapter.out.redis;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.RedisAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;

import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAuthAdapter implements RedisAuthPort {

    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private static final String AUTHORITIES_KEY = "authorities";
    private static final String BLACKLIST = "blacklisted";

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * RefreshToken 과 사용자 정보를 Redis에 저장
     */
    @Override
    public void storeRefreshToken(String userId, String refreshToken, String roles) {
        try {
            // 기존 데이터 삭제
            redisTemplate.delete(userId);

            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(userId, createTokenDataMap(refreshToken, roles));
            redisTemplate.expire(userId, 7, TimeUnit.DAYS);
        } catch (DataAccessException dae) {
            throw new AuthException(AuthErrorCode.REDIS_STORE_FAILURE, dae);
        }
    }

    /**
     * Refresh 토큰이 유효한지 확인
     */
    @Override
    public boolean isValidRefreshToken(String userId, String refreshToken) {
        try {
            String storedRefreshToken = (String)redisTemplate.opsForHash()
                .get(userId, REFRESH_TOKEN_KEY);
            return Objects.requireNonNull(storedRefreshToken).equals(refreshToken);
        } catch (DataAccessException dae) {
            throw new AuthException(AuthErrorCode.REDIS_RETRIEVE_FAILURE, dae);
        }
    }

    @Override
    public boolean isTokenBlacklisted(String accessToken) {
        try {
            // 액세스 토큰이 키로 존재하면 블랙리스트된 상태
            return redisTemplate.hasKey(accessToken);
        } catch (DataAccessException dae) {
            throw new AuthException(AuthErrorCode.REDIS_BLACKLIST_CHECK_FAIL, dae);
        }
    }

    /**
     * 로그아웃 시 액세스 토큰과 리프레시 토큰을 블랙리스트에 추가
     */
    @Override
    public void logoutTokens(String accessToken, long accessTokenExpiration, String userId) {
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
     * 사용자 권한 정보 가져오기
     */
    @Override
    public String getAuthorities(String userId) {
        try {
            return (String)redisTemplate.opsForHash().get(userId, AUTHORITIES_KEY);
        } catch (DataAccessException dae) {
            throw new AuthException(AuthErrorCode.REDIS_AUTHORITIES_RETRIEVE_FAIL, dae);
        }
    }

    private HashMap<String, Object> createTokenDataMap(String refreshToken, String authorities) {
        HashMap<String, Object> tokenDataMap = new HashMap<>();
        tokenDataMap.put(REFRESH_TOKEN_KEY, refreshToken);
        tokenDataMap.put(AUTHORITIES_KEY, authorities);
        return tokenDataMap;
    }

}
