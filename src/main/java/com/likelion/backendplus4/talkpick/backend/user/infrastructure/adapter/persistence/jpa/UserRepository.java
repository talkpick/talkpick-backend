package com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa;

import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

}
