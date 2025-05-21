package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.entity.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {
}
