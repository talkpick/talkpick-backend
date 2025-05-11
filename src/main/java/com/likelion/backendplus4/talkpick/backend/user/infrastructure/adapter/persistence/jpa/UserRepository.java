package com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa;

import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

	boolean existsByAccount(String account);
}
