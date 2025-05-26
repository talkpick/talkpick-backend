package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.support.mapper;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.support.mapper.ChatMessageResponseMapper;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;


/**
 * 도메인 모델의 ChatMessage 리스트와 페이징 정보(hasNext)를 기반으로
 * ChatMessageResponse 객체들의 SliceResponse를 생성하는 매퍼 클래스입니다.
 *
 * @since 2025-05-26
 */
public class SliceResponseChatMapper {


	/**
	 * ChatMessage 도메인 객체 리스트와 다음 페이지 존재 여부를 받아
	 * ChatMessageResponse 리스트를 포함하는 SliceResponse를 생성합니다.
	 *
	 * @param chatMessages 변환할 ChatMessage 도메인 객체 리스트
	 * @param hasNext      다음 페이지 존재 여부
	 * @return ChatMessageResponse 객체 리스트 및 페이징 정보를 담은 SliceResponse
	 * @author 박찬병
	 * @since 2025-05-26
	 */
	public static SliceResponse<ChatMessageResponse> toSliceResponse(List<ChatMessage> chatMessages, boolean hasNext) {
		List<ChatMessageResponse> result = chatMessages
			.stream()
			.map(ChatMessageResponseMapper::toResponseFromDomain)
			.toList();
		return new SliceResponse<>(result, hasNext);
	}
}
