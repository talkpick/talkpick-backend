package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

/**
 * 채팅 메시지를 JPA를 통해 DB에 저장하거나 조회하는 어댑터입니다.
 *
 * @since 2025-05-23
 */
public interface ChatMessageDbPort {

	/**
	 * 채팅 메시지 리스트를 엔티티로 변환하여 DB에 일괄 저장합니다.
	 *
	 * @param chatMessage 저장할 채팅 메시지 리스트
	 * @author 박찬병
	 * @since 2025-05-23
	 */
	void saveAll(List<ChatMessage> chatMessage);

	/**
	 * 지정한 게시글 ID에 대한 최신 채팅 메시지를 ID 역순으로 최대 N개 조회합니다.
	 *
	 * @param articleId 게시글 식별자
	 * @param maxCacheSize 조회할 최대 메시지 수
	 * @return 조회된 최신 채팅 메시지 리스트
	 * @author 박찬병
	 * @since 2025-05-23
	 */
	Slice<ChatMessage> findRecentMessages(String articleId, PageRequest pageRequest);

	Slice<ChatMessage> findBeforeMessages(String articleId, LocalDateTime before, PageRequest of);
}
