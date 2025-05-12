package com.likelion.backendplus4.talkpick.backend.auth.application.port.out;

import java.util.Optional;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;

public interface UserJpaRepoPort {

    Optional<AuthUser> findUserByAccount(String account);

    void existsByAccountAndEmail(String account);

    void saveUser(AuthUser authUser);

    void deleteUser(Long id);
}
