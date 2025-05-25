package com.likelion.backendplus4.talkpick.backend.chat.application.port.in;


import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatMessageResponse;
import com.likelion.backendplus4.talkpick.backend.common.response.SliceResponse;


public interface ChatUseCase {

    /**
     * 사용자가 채팅 메시지를 보낼 때 호출됩니다.
     * - 메시지 타입이 CHAT 인 경우
     *   - Redis 캐시에 저장
     *   - Redis 스트림에 저장
     * - RabbitMQ에 발행
     * @param message 채팅 메시지
     * @author 박찬병
     * @since 2025-05-20
     */
    void sendMessage(ChatMessage message);

    /**
     * 캐시에서 최근 채팅 메시지를 조회하고, 메시지가 없으면 DB에서 로드하여 캐시에 저장한 뒤 반환합니다.
     *
     * @param articleId 게시글 식별자
     * @return 채팅 메시지 슬라이스 응답 (메시지 목록과 다음 페이지 여부)
     * @author 박찬병
     * @since 2025-05-25
     */
    SliceResponse<ChatMessageResponse> getChatMessage(String articleId);

    /**
     * 지정된 기준 ID(beforeId) 이전의 채팅 메시지를 DB에서 조회하여 반환합니다.
     *
     * @param articleId 게시글 식별자
     * @param beforeId  이 ID 이전의 메시지들을 조회
     * @param limit     조회할 메시지 개수
     * @return 채팅 메시지 슬라이스 응답 (메시지 목록과 다음 페이지 여부)
     * @author 박찬병
     * @since 2025-05-25
     */
    SliceResponse<ChatMessageResponse> loadOlderMessages(String articleId, Long beforeId, int limit);
}
