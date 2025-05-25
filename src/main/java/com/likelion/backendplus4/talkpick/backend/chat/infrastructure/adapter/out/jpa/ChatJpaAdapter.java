package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa;

import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageDbPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.entity.ChatMessageEntity;
import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.repository.ChatMessageRepository;
import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.support.mapper.ChatMessageMapper;

import lombok.RequiredArgsConstructor;

/**
 * 채팅 메시지를 JPA를 통해 DB에 저장하거나 조회하는 어댑터입니다.
 *
 * @since 2025-05-23
 */
@Component
@RequiredArgsConstructor
public class ChatJpaAdapter implements ChatMessageDbPort {

	private final ChatMessageRepository repository;

	/**
	 * 채팅 메시지 리스트를 엔티티로 변환하여 DB에 일괄 저장합니다.
	 *
	 * @param chatMessage 저장할 채팅 메시지 리스트
	 * @author 박찬병
	 * @since 2025-05-23
	 */
	@Override
	public void saveAll(List<ChatMessage> chatMessage) {
		List<ChatMessageEntity> chatEntity = chatMessage.stream()
			.map(ChatMessageMapper::toEntityFromDomain)
			.toList();
		repository.saveAll(chatEntity);
	}

	/**
	 * 지정한 게시글 ID에 대한 최신 채팅 메시지를 ID 역순으로 최대 N개 조회합니다.
	 *
	 * @param articleId 게시글 식별자
	 * @param pageRequest
	 * @return 조회된 최신 채팅 메시지 리스트
	 * @author 박찬병
	 * @since 2025-05-23
	 */
	@Override
	public Slice<ChatMessage> findRecentMessages(String articleId, PageRequest pageRequest) {
		return repository.findByArticleIdOrderByIdDesc(articleId, pageRequest)
			.map(ChatMessageMapper::toDomainFromEntity);

	}

	@Override
	public Slice<ChatMessage> findBeforeMessages(String articleId, Long beforeId, PageRequest of) {
		Slice<ChatMessageEntity> entitySlice = repository.findByArticleIdAndIdLessThanOrderByIdDesc(
			articleId, beforeId, of);
		return entitySlice.map(ChatMessageMapper::toDomainFromEntity);
	}
}
