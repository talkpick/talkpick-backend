package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

/**
 * 메시지를 XADD 하는 책임만을 가진 Port
 */
public interface ChatMessageStreamPort {

	/**
	 * 채팅 메시지를 Redis Stream에 저장합니다.
	 *
	 * @param message 저장할 채팅 메시지 도메인 객체
	 * @author 박찬병
	 * @since 2025-05-22
	 */
	void cacheToStream(ChatMessage message);

}
