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
	 * 지정한 게시글 ID에 대한 최신 채팅 메시지를 ID 역순으로 최대 N개 조회합니다.
	 *
	 * @param articleId 게시글 식별자
	 * @param pageRequest 페이징 정보를 담고 있는 PageRequest 객체
	 * @return 조회된 최신 채팅 메시지 리스트
	 * @author 박찬병
	 * @since 2025-05-23
	 */
	Slice<ChatMessage> findRecentMessages(String articleId, PageRequest pageRequest);

	/**
	 * 지정된 게시글(articleId)에서 주어진 시점(before) 이전의 채팅 메시지를
	 * 내림차순(timestamp 기준)으로 페이징 조회하여 Slice로 반환합니다.
	 *
	 * @param articleId 조회할 채팅 메시지의 게시글 식별자
	 * @param before    조회 기준이 되는 날짜·시간 (이 시점 이전의 메시지들을 조회)
	 * @param of        페이징 설정을 담고 있는 PageRequest
	 * @return 조회된 ChatMessage 도메인 객체들의 Slice
	 * @author 박찬병
	 * @since 2025-05-26
	 */
	Slice<ChatMessage> findBeforeMessages(String articleId, LocalDateTime before, PageRequest of);
}
