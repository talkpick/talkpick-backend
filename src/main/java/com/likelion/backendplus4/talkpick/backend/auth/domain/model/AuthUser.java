package com.likelion.backendplus4.talkpick.backend.auth.domain.model;

import lombok.Builder;
import lombok.Getter;

/**
 * 사용자와 관련된 도메인 모델.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
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

    /**
     * 비밀번호를 인코딩된 값으로 업데이트합니다.
     *
     * @param encodePassword 인코딩된 비밀번호
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    public void updateEncodedPassword(String encodePassword) {
        this.password = encodePassword;
    }
}