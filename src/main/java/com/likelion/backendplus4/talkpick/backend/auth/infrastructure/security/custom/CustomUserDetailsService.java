package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.CustomUserDetailsMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAuthPort userAuthPort;

    @Override
    public UserDetails loadUserByUsername(String id) {
        AuthUser authUser = userAuthPort.findUserById(Long.valueOf(id))
            .orElseThrow(() -> new AuthException(AuthErrorCode.AUTHENTICATION_FAILED));
        return CustomUserDetailsMapper.toCustomUserDetails(authUser);
    }

}
