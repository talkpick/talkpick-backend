package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

/**
 * 메시지를 XADD 하는 책임만을 가진 Port
 */
public interface ChatMessageStreamPort {

	void cacheToStream(ChatMessage message);

}
