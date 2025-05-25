package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatSessionPort;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatCountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인메모리 채팅 세션을 관리 클래스
 *
 * @since 2025-05-24
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryChatSessionAdapter implements ChatSessionPort {

    private final ConcurrentHashMap<String, Set<String>> sessions = new ConcurrentHashMap<>(); // 채팅 방 별 세션관리 맵
    private final ConcurrentHashMap<String, String> reverse = new ConcurrentHashMap<>(); // 세션이 속한 채팅방 관리 맵
    private final SimpMessagingTemplate wsTemplate;

    private static final String CHAT_TOPIC_PREFIX = "/topic/chat.";

    /**
     * 지정된 뉴스ID에 채팅 세션을 추가한다.
     *
     * @param articleId 뉴스 식별자
     * @param sessionId 세션 식별자
     * @author 이해창
     * @since 2025-05-24
     */
    @Override
    public void addSession(String articleId, String sessionId) {
        createRoomIfNotExists(articleId);
        sessions.get(articleId).add(sessionId);
        reverse.put(sessionId, articleId);
        int count = getSessionCount(articleId);

        log.info("세션 {} 가 {} 채팅방에 입장하였습니다, 인원수 {}", sessionId, articleId, count);

        String destination = CHAT_TOPIC_PREFIX + articleId + ".count";
        ChatCountResponse response = new ChatCountResponse(count);
        wsTemplate.convertAndSend(destination, response);
    }

    /**
     * 주어진 세션 ID를 기반으로 채팅 세션을 제거한다.
     *
     * @param sessionId 세션 식별자
     * @author 이해창
     * @since 2025-05-24
     */
    @Override
    public void removeSession(String sessionId) {
        String articleId = reverse.get(sessionId);
        if (articleId == null) {
            return;
        }
        reverse.remove(sessionId);
        Set<String> roomSessions = sessions.get(articleId);
        if (roomSessions != null) {
            roomSessions.remove(sessionId);
            int count = getSessionCount(articleId);
            if (count == 0) {
                sessions.remove(articleId);
                log.info("채팅방 {} 가 비어 있어 채팅방 맵에서 삭제되었습니다.", articleId);
            }
            log.info("세션 {} 가 {} 채팅방에서 퇴장하였습니다, 인원수 {}", sessionId, articleId, count);
            String destination = CHAT_TOPIC_PREFIX + articleId + ".count";
            ChatCountResponse response = new ChatCountResponse(count);
            wsTemplate.convertAndSend(destination, response);
        }
    }

    /**
     * 특정 뉴스에 연결된 채팅 세션 수를 반환한다.
     *
     * @param articleId 뉴스 식별자
     * @return 연결된 세션 수
     * @author 이해창
     * @since 2025-05-24
     */
    @Override
    public int getSessionCount(String articleId) {
        return sessions.getOrDefault(articleId, Collections.emptySet()).size();
    }

    /**
     * 세션이 존재하지 않으면 새로 생성한다.
     *
     * @param articleId 뉴스 식별자
     * @author 이해창
     * @since 2025-05-24
     */
    private void createRoomIfNotExists(String articleId) {
        if (!sessions.containsKey(articleId)) {
            sessions.put(articleId, ConcurrentHashMap.newKeySet());
        }
    }
}
