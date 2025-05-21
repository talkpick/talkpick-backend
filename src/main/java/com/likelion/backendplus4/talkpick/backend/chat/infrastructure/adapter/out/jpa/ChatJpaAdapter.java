package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa;

import java.util.List;

import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageDbPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.entity.ChatMessageEntity;
import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out.jpa.repository.ChatMessageRepository;
import com.likelion.backendplus4.talkpick.backend.chat.infrastructure.support.mapper.ChatMessageEntityMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatJpaAdapter implements ChatMessageDbPort {

	private final ChatMessageRepository repository;

	@Override
	public void saveAll(List<ChatMessage> chatMessage) {
		List<ChatMessageEntity> chatEntity = chatMessage.stream()
			.map(ChatMessageEntityMapper::toEntityFromDomain)
			.toList();
		repository.saveAll(chatEntity);
	}
}
