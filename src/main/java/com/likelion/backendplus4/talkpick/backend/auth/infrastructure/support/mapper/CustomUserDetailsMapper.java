package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.user.CustomUserDetails;

import io.jsonwebtoken.Claims;

/**
 * AuthUser 및 JWT Claims → CustomUserDetails 매핑.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public class CustomUserDetailsMapper {

    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * AuthUser 에서 CustomUserDetails 로 변환합니다.
     *
     * @param user AuthUser 도메인 객체
     * @return CustomUserDetails
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-15
     */
    public static CustomUserDetails toCustomUserDetails(AuthUser user) {
        return CustomUserDetails.builder()
            .username(String.valueOf(user.getUserId()))
            .password(user.getPassword())
            .nickname(user.getNickName())
            .authority(ROLE_PREFIX + user.getRole())
            .build();
    }

    /**
     * Claims 에서 CustomUserDetails 로 변환합니다.
     *
     * @param claims JWT Claims
     * @return CustomUserDetails
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-15
     */
    public static CustomUserDetails fromClaims(Claims claims) {
        String subject = claims.getSubject();
        List<GrantedAuthority> auths = extractAuthorities(claims);
        String nickName = claims.get("nickName", String.class);

        return CustomUserDetails.builder()
            .username(subject)
            .nickname(nickName)
            .authority(auths.getFirst().getAuthority())
            .build();
    }

    /**
     * Claims에서 역할 문자열을 추출하고 GrantedAuthority 리스트로 변환합니다.
     *
     * 1. Claims에서 'roles' 속성 조회
     * 2. 쉼표 구분자로 분리
     * 3. ROLE_ 접두사 보장 후 SimpleGrantedAuthority 객체 생성
     * 4. 빈 리스트일 경우 기본 ROLE_USER 권한 설정
     *
     * @param claims JWT Claims
     * @return 변환된 권한 리스트
     * @since 2025-05-12
     * @modified 2025-05-12
     * @author 박찬병
     */
    private static List<GrantedAuthority> extractAuthorities(Claims claims) {
        String roles = claims.get("roles", String.class);
        List<GrantedAuthority> auths = Stream.of(Optional.ofNullable(roles).orElse("")
                .split(","))
            .filter(s -> !s.isBlank())
            .map(r -> new SimpleGrantedAuthority(
                r.startsWith(ROLE_PREFIX) ? r : ROLE_PREFIX + r))
            .collect(Collectors.toList());
        return auths.isEmpty()
            ? List.of(new SimpleGrantedAuthority(ROLE_PREFIX + "USER"))
            : auths;
    }
}