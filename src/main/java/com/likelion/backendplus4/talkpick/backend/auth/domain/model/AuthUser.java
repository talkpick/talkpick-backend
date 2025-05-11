package com.likelion.backendplus4.talkpick.backend.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthUser {

    private Long userId;

    private String account;

    private String password;

    private String name;

    private String nickName;

    private String email;

    private String role;

    public void updateEncodedPassword(String encodePassword) {
        this.password = encodePassword;
    }
}
