package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.persistence.adapter.out.jpa;

import java.util.Optional;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.AuthUserMapper;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserAuthJpaAdapter implements UserAuthPort {

    private final UserRepository userRepository;

    @Override
    public Optional<AuthUser> findUserById(Long id) {
        return userRepository.findById(id)
            .map(AuthUserMapper::toDomain);
    }
}
