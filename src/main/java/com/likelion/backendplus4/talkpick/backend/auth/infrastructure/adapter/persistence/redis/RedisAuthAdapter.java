package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.adapter.persistence.redis;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.RedisAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.dto.RefreshTokenInfoDto;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisAuthAdapter implements RedisAuthPort {

    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * RefreshToken 과 사용자 정보를 Redis에 저장
     * @param tokenData 리프래시 토큰 및 사용자 정보
     */
    @Override
    public void storeRefreshToken(RefreshTokenInfoDto tokenData) {
        try {
            // 기존 데이터 삭제
            redisTemplate.delete(tokenData.id());

            HashOperations<String, Object, Object> hashOperations = redisTemplate.opsForHash();
            hashOperations.putAll(tokenData.id(), createTokenDataMap(tokenData));
            redisTemplate.expire(tokenData.id(), 7, TimeUnit.DAYS);
        } catch (Exception e) {
            log.warn("Redis에 Refresh Token 저장 실패: {}", e.getMessage());
        }
    }

    /**
     * Refresh 토큰이 유효한지 확인
     */
    @Override
    public boolean isValidRefreshToken(String userId, String refreshToken) {
        try {
            String storedRefreshToken = (String) redisTemplate.opsForHash()
                    .get(userId, REFRESH_TOKEN_KEY);
            return storedRefreshToken.equals(refreshToken);
        } catch (Exception e) {
            log.warn("Redis에서 Refresh Token 검증 실패: {}", e.getMessage());
            return false;
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
                    "blacklisted",
                    accessTokenExpiration,
                    TimeUnit.MILLISECONDS);
            redisTemplate.delete(userId);
        } catch (Exception e) {
            log.warn("Redis에서 로그아웃 처리 실패: {}", e.getMessage());
        }
    }

    /**
     * 사용자 권한 정보 가져오기
     */
    @Override
    public String getAuthorities(String userId) {
        try {
            return (String) redisTemplate.opsForHash().get(userId, "authorities");
        } catch (Exception e) {
            log.warn("Redis에서 권한 정보 조회 실패: {}", e.getMessage());
            return null;
        }
    }

    private HashMap<String, Object> createTokenDataMap(RefreshTokenInfoDto tokenData) {
        HashMap<String, Object> tokenDataMap = new HashMap<>();
        tokenDataMap.put(REFRESH_TOKEN_KEY, tokenData.refreshToken());
        tokenDataMap.put("authorities", tokenData.authorities());
        return tokenDataMap;
    }

}
