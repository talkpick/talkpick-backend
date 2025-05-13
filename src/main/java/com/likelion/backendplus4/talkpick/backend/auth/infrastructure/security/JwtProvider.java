package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.AuthTokenStorePort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.TokenMapper;

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

/**
 * JWT 토큰 생성, 갱신, 파싱 기능을 제공하는 컴포넌트.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {

    private final JwtVerifier jwtVerifier;
    private final Key jwtSigningKey;
    private final AuthTokenStorePort authTokenStorePort;

    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 30;         // 30분
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7; // 7일

    /**
     * 인증 정보를 바탕으로 액세스·리프레시 토큰을 생성하고,
     * Redis에 리프레시 토큰을 저장합니다.
     *
     * 1. Authentication에서 사용자 ID, 권한 문자열 추출
     * 2. 액세스 토큰 생성
     * 3. 리프레시 토큰 생성
     * 4. Redis에 리프레시 토큰과 권한 정보 저장
     * 5. 토큰 쌍(TokenPair) 반환
     *
     * @param authentication Spring Security 인증 정보
     * @return 생성된 액세스·리프레시 토큰 쌍
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    public TokenPair generateToken(Authentication authentication) {
        String userId = authentication.getName();
        String roles = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.joining(","));

        String accessToken = createToken(userId, roles, ACCESS_TOKEN_EXPIRATION);

        String refreshToken = createToken(userId, null, REFRESH_TOKEN_EXPIRATION);

        authTokenStorePort.storeRefreshToken(userId, refreshToken, roles);

        return TokenMapper.toDomain(accessToken, refreshToken);
    }

    /**
     * 리프레시 토큰을 검증하고 새로운 액세스 토큰을 발급합니다.
     *
     * 1. 리프레시 토큰 유효성 검증
     * 2. 토큰에서 사용자 ID 추출
     * 3. Redis에 저장된 리프레시 토큰 일치 여부 확인
     * 4. Redis에서 권한 정보 조회
     * 5. 새로운 액세스 토큰 생성 및 반환 (리프레시 토큰은 유지)
     *
     * @param refreshToken 클라이언트로부터 제출된 리프레시 토큰
     * @return 갱신된 액세스 토큰과 기존 리프레시 토큰 쌍
     * @throws AuthException 리프레시 토큰이 유효하지 않을 경우 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    public TokenPair refreshAccessToken(String refreshToken) {
        jwtVerifier.verifyToken(refreshToken);

        String userId = getUserIdFromToken(refreshToken);

        validateRefreshToken(userId, refreshToken);

        String authorities = authTokenStorePort.getAuthorities(userId);

        String newAccessToken = createToken(userId, authorities, ACCESS_TOKEN_EXPIRATION);
        return TokenMapper.toDomain(newAccessToken, refreshToken);
    }

    /**
     * 토큰에서 사용자 ID(Subject)를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰에 설정된 Subject (사용자 ID)
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    public String getUserIdFromToken(String token) {
        return jwtVerifier.verifyToken(token).getSubject();
    }

    /**
     * 토큰의 남은 만료 시간을 계산하여 반환합니다.
     *
     * 1. Claims에서 만료 날짜 조회
     * 2. 현재 시각과의 차이 계산
     *
     * @param token JWT 토큰
     * @return 남은 만료 시간 (밀리초)
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    public long getExpiration(String token) {
        Claims claims = jwtVerifier.verifyToken(token);
        return claims.getExpiration().getTime() - System.currentTimeMillis();
    }

    /**
     * JWT 토큰 생성의 공통 로직을 수행합니다.
     *
     * @param userId         토큰 Subject
     * @param roles          토큰에 포함할 권한 문자열 (nullable)
     * @param validityMillis 토큰 유효 기간 (밀리초)
     * @return 생성된 JWT 토큰 문자열
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
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

    /**
     * JWT Claims를 구성합니다.
     *
     * 1. Subject에 사용자 ID 설정
     * 2. roles가 존재하면 Claims에 추가
     *
     * @param userId 사용자 ID
     * @param roles  권한 문자열 (nullable)
     * @return 설정된 Claims 객체
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    private Claims buildClaims(String userId, String roles) {
        Claims claims = Jwts.claims().setSubject(userId);
        if (roles != null && !roles.isBlank()) {
            claims.put("roles", roles);
        }
        return claims;
    }

    /**
     * Redis에 저장된 리프레시 토큰 일치 여부를 확인하고, 유효하지 않으면 예외를 던집니다.
     *
     * 1. Redis에 저장된 토큰 조회
     * 2. 제출된 리프레시 토큰과 비교
     *
     * @param userId       사용자 식별자
     * @param refreshToken 클라이언트가 제출한 리프레시 토큰
     * @throws AuthException 리프레시 토큰이 유효하지 않을 경우 발생
     * @since 2025-05-14
     * @modified 2025-05-14
     * @author 박찬병
     */
    private void validateRefreshToken(String userId, String refreshToken) {
        if (!authTokenStorePort.isValidRefreshToken(userId, refreshToken)) {
            throw new AuthException(AuthErrorCode.INVALID_REFRESH_TOKEN);
        }
    }
}