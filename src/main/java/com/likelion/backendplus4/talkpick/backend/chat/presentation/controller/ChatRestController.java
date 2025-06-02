package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller;


import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.docs.ChatRestControllerDocs;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRestController implements ChatRestControllerDocs {

	private final ChatUseCase chatUseCase;

	@Override
	@GetMapping("/{articleId}/messages")
	public ResponseEntity<ApiResponse<SliceResponse<ChatMessageResponse>>> getMessages(
		@PathVariable String articleId
	) {
		SliceResponse<ChatMessageResponse> chatMessage = chatUseCase.getChatMessage(articleId);
		return ApiResponse.success(chatMessage);
	}

	/**
	 * before 이전의 과거 메시지 페이징 조회
	 *
	 * @param articleId 채팅방 식별자
	 * @param before  기준 메시지 ID (이 ID 미만의 메시지만)
	 * @param limit     최대 조회 개수 (기본 20)
	 * @return ChatMessageResponse 리스트 + more flag
	 */
	@Override
	@GetMapping("/{articleId}/messages/older")
	public ResponseEntity<ApiResponse<SliceResponse<ChatMessageResponse>>> loadOlderMessages(
		@PathVariable String articleId,
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		@RequestParam LocalDateTime before,
		@RequestParam(defaultValue = "100") int limit
	) {
		SliceResponse<ChatMessageResponse> chatMessage = chatUseCase.loadOlderMessages(articleId,
			before, limit);
		return ApiResponse.success(chatMessage);
	}

}
