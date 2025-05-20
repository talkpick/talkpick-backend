package com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.repository;

import java.util.Optional;

import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByAccount(String account);

	Optional<UserEntity> findUserByAccount(String account);

	boolean existsByEmail(String email);

	boolean existsByNickName(String nickname);

	Optional<UserEntity> findUserByNameAndEmail(String name, String email);
}
