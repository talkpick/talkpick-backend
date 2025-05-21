package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/chat")
public class ChatRestController {

	private final ChatUseCase chatUseCase;

	@GetMapping("/{articleId}/messages")
	public ResponseEntity<ApiResponse<SliceResponse<ChatMessageResponse>>> getMessages(
		@PathVariable String articleId
	) {
		SliceResponse<ChatMessageResponse> chatMessage = chatUseCase.getChatMessage(articleId);
		return ApiResponse.success(chatMessage);
	}

}
