package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.entity.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

	Slice<ChatMessageEntity> findByArticleIdOrderByIdDesc(
		String articleId,
		Pageable pageable
	);

	// beforeId 이전 메시지
	Slice<ChatMessageEntity> findByArticleIdAndIdLessThanOrderByIdDesc(
		String articleId,
		Long beforeId,
		Pageable pageable
	);

}
