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

public class CustomUserDetailsMapper {

    private static final String ROLE_PREFIX = "ROLE_";

    public static CustomUserDetails toCustomUserDetails(AuthUser user) {
        return CustomUserDetails.builder()
                .username(String.valueOf(user.getUserId()))
                .password(user.getPassword())
                .authority(ROLE_PREFIX + user.getRole())
                .build();
    }

    public static CustomUserDetails fromClaims(Claims claims) {
        String subject = claims.getSubject();
        String roles = claims.get("roles", String.class);
        List<GrantedAuthority> auths = Stream.of(Optional.ofNullable(roles).orElse("")
                .split(","))
            .filter(s -> !s.isBlank())
            .map(r -> new SimpleGrantedAuthority(r.startsWith(ROLE_PREFIX) ? r : ROLE_PREFIX + r))
            .collect(Collectors.toList());

        return CustomUserDetails.builder()
            .username(subject)
            .authority(auths.isEmpty() ? ROLE_PREFIX + "USER" : auths.get(0).getAuthority())
            .build();
    }

}
