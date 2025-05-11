package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.user;

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
    public UserDetails loadUserByUsername(String account) {
        AuthUser authUser = userAuthPort.findUserByAccount(account)
            .orElseThrow(() -> new AuthException(AuthErrorCode.AUTHENTICATION_FAILED));
        return CustomUserDetailsMapper.toCustomUserDetails(authUser);
    }

}
