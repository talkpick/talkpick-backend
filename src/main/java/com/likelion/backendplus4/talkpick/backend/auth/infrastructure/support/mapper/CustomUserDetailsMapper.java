package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.CustomUserDetails;

public class CustomUserDetailsMapper {

    private static final String ROLE_PREFIX = "ROLE_";

    public static CustomUserDetails toCustomUserDetails(AuthUser user) {
        return CustomUserDetails.builder()
                .username(String.valueOf(user.getUserId()))
                .password(user.getPassword())
                .authority(ROLE_PREFIX + user.getRole())
                .build();
    }

}
