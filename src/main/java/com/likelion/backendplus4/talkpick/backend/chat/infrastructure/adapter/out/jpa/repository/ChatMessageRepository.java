package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.repository;

import java.time.LocalDateTime;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.entity.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

	Slice<ChatMessageEntity> findByArticleIdOrderByIdDesc(
		String articleId,
		Pageable pageable
	);

	Slice<ChatMessageEntity> findByArticleIdAndTimestampBeforeOrderByTimestampDesc(
		String articleId,
		LocalDateTime before,
		Pageable pageable
	);

}
