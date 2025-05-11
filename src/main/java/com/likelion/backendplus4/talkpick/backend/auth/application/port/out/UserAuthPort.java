package com.likelion.backendplus4.talkpick.backend.auth.application.port.out;

import java.util.Optional;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;

public interface UserAuthPort {

    Optional<AuthUser> findUserById(Long id);

    void existsByAccountAndEmail(String account);

    void saveUser(AuthUser authUser);

    void deleteUser(Long id);
}
