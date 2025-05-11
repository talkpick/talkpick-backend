package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.RedisAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.dto.JwtToken;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.dto.RefreshTokenInfoDto;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtVerifier jwtVerifier;
    private final Key jwtSigningKey;
    private final RedisAuthPort redisAuthPort;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30; // 30분
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    public JwtToken generateToken(Authentication authentication) {
        String userId = authentication.getName();
        String roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        String accessToken = createToken(userId, roles, ACCESS_TOKEN_EXPIRATION);
        String refreshToken = createToken(userId, null, REFRESH_TOKEN_EXPIRATION);

        // Redis에 refreshToken 저장
        redisAuthPort.storeRefreshToken(
            RefreshTokenInfoDto.of(userId, refreshToken, roles)
        );

        return JwtToken.of(accessToken, refreshToken);
    }

    /**
     * Refresh 토큰을 이용한 Access 토큰 갱신
     */
    public JwtToken refreshAccessToken(String refreshToken) {
        // 1. 토큰 유효성 검사
        jwtVerifier.verifyToken(refreshToken);

        // 2. 토큰에서 사용자 ID 추출
        String userId = getUserIdFromToken(refreshToken);

        // 3. Redis에서 Refresh 토큰 유효성 검사
        if (!redisAuthPort.isValidRefreshToken(userId, refreshToken)) {
            //            throw new BusinessException(refreshToken, "refreshToken", ErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 4. Redis에서 사용자 권한 정보 추출
        String authorities = redisAuthPort.getAuthorities(userId);

        // 5. 새로운 Access 토큰 생성
        String newAccessToken = createToken(userId, authorities, ACCESS_TOKEN_EXPIRATION);

        // 7. 새로운 AuthResponseDto 반환 (기존 Refresh 토큰 유지)
        return JwtToken.of(newAccessToken, refreshToken);
    }


    /**
     * 토큰에서 만료 시간 가져오기
     *
     * @param token JWT 토큰
     * @return 토큰의 남은 만료 시간 (밀리초)
     */
    public long getExpiration(String token) {
        Claims claims = jwtVerifier.verifyToken(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    // 공통 토큰 생성 로직
    private String createToken(String userId, String roles, long validityMillis) {
        Claims claims = buildClaims(userId, roles);
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + validityMillis);

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(expiresAt)
            .signWith(jwtSigningKey, SignatureAlgorithm.HS256)
            .compact();
    }

    // 토큰에서 사용자 이름을 추출하는 메서드
    private String getUserIdFromToken(String token) {
        return jwtVerifier.verifyToken(token).getSubject();
    }

    private Claims buildClaims(String userId, String roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        if (roles != null && !roles.isBlank()) {
            claims.put("roles", roles);
        }
        return claims;
    }

}
