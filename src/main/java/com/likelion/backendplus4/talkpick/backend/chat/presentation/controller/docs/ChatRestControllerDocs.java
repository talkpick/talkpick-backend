package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.docs;

import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

@Tag(
	name = "Chat",
	description = "채팅 메시지 조회 API"
)
public interface ChatRestControllerDocs {

	@Operation(
		summary = "최신 채팅 메시지 조회",
		description = "지정된 채팅방(articleId)의 최신 메시지를 조회합니다.",
		security = @SecurityRequirement(name = "BearerAuth")
	)
	ResponseEntity<ApiResponse<SliceResponse<ChatMessageResponse>>> getMessages(
		@Parameter(
			in = ParameterIn.PATH,
			description = "채팅방 식별자",
			required = true,
			example = "article123"
		)
		String articleId
	);

	@Operation(
		summary = "과거 채팅 메시지 페이징 조회",
		description = "지정된 채팅방(articleId)에서 주어진 기준(`before`) 이전의 메시지를 최대 `limit`개 조회합니다.",
		security = @SecurityRequirement(name = "BearerAuth")
	)
	ResponseEntity<ApiResponse<SliceResponse<ChatMessageResponse>>> loadOlderMessages(
		@Parameter(
			in = ParameterIn.PATH,
			description = "채팅방 식별자",
			required = true,
			example = "article123"
		)
		String articleId,

		@Parameter(
			in = ParameterIn.QUERY,
			description = "조회 기준이 되는 메시지의 생성일시 (ISO-8601)",
			required = true,
			example = "2025-06-01T15:30:00"
		)
		LocalDateTime before,

		@Parameter(
			in = ParameterIn.QUERY,
			description = "최대 조회 개수 (기본값: 100)",
			example = "50"
		)
		int limit
	);
}