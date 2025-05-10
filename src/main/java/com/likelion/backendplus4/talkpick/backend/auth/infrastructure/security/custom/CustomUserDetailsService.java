package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom;

import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.entity.UserEntity;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String id) throws UsernameNotFoundException {
        return userRepository.findById(Long.valueOf(id))
                .map(this::createUserDetails)
                .orElseThrow(() -> new BusinessException(id, "username", ErrorCode.AUTHENTICATION_FAILED));
    }

    private UserDetails createUserDetails(UserEntity user) {
        return CustomUserDetails.builder()
                .username(String.valueOf(user.getId()))
                .password(user.getPassword())
                .authority("ROLE_" + user.getRole())
                .build();
    }
}
