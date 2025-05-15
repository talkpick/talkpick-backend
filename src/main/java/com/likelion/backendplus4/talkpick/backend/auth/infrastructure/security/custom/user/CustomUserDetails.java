package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.user;

import java.util.ArrayList;
import java.util.Collection;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Spring Security에서 인증 시 사용하는 커스텀 UserDetails 구현체.
 *
 * @author 박찬병
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Getter
@Builder
public class CustomUserDetails implements UserDetails {

    private String username;
    private String nickname;
    private String password;
    private String authority;

    /**
     * 사용자 권한 정보를 GrantedAuthority 컬렉션으로 반환합니다.
     *
     * @return 권한이 담긴 GrantedAuthority 컬렉션
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(authority));
        return auth;
    }

}
