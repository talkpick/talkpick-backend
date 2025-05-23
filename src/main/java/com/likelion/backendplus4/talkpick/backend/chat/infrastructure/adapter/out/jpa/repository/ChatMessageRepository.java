package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.entity.ChatMessageEntity;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, Long> {

	@Query("""
		   SELECT m
		   FROM ChatMessageEntity m
		   WHERE m.articleId = :artricleId
		   ORDER BY m.id DESC
		   LIMIT :limit
		   """)
	List<ChatMessageEntity> findTopNByArticleIdOrderByIdDesc(@Param("articleId") String articleId, @Param("limit") int maxCacheSize);
}
