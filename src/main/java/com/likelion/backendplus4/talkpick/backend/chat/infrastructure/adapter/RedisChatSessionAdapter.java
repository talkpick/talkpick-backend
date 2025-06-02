package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatSessionPort;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatCountResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 인메모리 기반으로 채팅 세션을 관리하는 어댑터 클래스
 * <p>
 * 채팅방 별로 연결된 세션을 관리하며, 세션 입장/퇴장 시 사용자 수를 실시간으로 브로드캐스팅한다.
 * <p>
 * 내부적으로 두 개의 맵을 사용하여 세션을 관리한다:<br/>
 * - sessions: 뉴스(articleId) 기준으로 연결된 세션 ID들을 저장하여 채팅방 인원수를 추적<br/>
 * - reverse: 세션 ID 기준으로 어떤 뉴스에 연결되어 있는지 역추적하여 빠르게 제거 처리 가능
 *
 * @since 2025-05-24
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Primary
public class RedisChatSessionAdapter implements ChatSessionPort {

    private final RedisTemplate<String, String> redisTemplate;
    private final SimpMessagingTemplate wsTemplate;

    private static final String CHAT_TOPIC_PREFIX = "/topic/chat.";
    private static final String SESSION_KEY_PREFIX = "chat:room:";    // e.g. chat:room:123 -> Set<sessionId>
    private static final String REVERSE_KEY = "chat:reverse";

    /**
     * 사용자가 특정 뉴스 채팅방에 입장했을 때 세션을 등록하고,
     * 전체 접속 인원 수를 해당 뉴스의 .count 토픽으로 실시간 전송한다.
     *
     * @param articleId 뉴스 식별자
     * @param sessionId 사용자의 웹소켓 세션 ID
     * @author 이해창
     * @since 2025-05-24
     */
    @Override
    public void addSession(String articleId, String sessionId) {
        String roomKey = SESSION_KEY_PREFIX + articleId;
        redisTemplate.opsForSet().add(roomKey, sessionId);
        redisTemplate.opsForHash().put(REVERSE_KEY, sessionId, articleId);
        int count = getSessionCount(articleId);

        log.info("세션 {} 가 {} 채팅방에 입장하였습니다, 인원수 {}", sessionId, articleId, count);

        sendCountToClients(articleId, count);
    }

    /**
     * 사용자가 채팅방에서 퇴장하거나 연결이 종료되었을 때 세션을 제거하고,
     * 남은 접속 인원 수를 .count 토픽으로 실시간 전송한다.
     * 모든 사용자가 퇴장하면 해당 채팅방에 대한 메모리 정보도 제거한다.
     *
     * @param sessionId 사용자의 웹소켓 세션 ID
     * @author 이해창
     * @since 2025-05-24
     */
    @Override
    public void removeSession(String sessionId) {
        String articleId = (String) redisTemplate.opsForHash().get(REVERSE_KEY, sessionId);
        if (articleId == null) {
            return;
        }
        String roomKey = SESSION_KEY_PREFIX + articleId;
        redisTemplate.opsForSet().remove(roomKey, sessionId);
        redisTemplate.opsForHash().delete(REVERSE_KEY, sessionId);

        Long size = redisTemplate.opsForSet().size(roomKey);
        int count = size != null ? size.intValue() : 0;
        if (count == 0) {
            redisTemplate.delete(roomKey);
            log.info("채팅방 {} 가 비어 있어 채팅방 맵에서 삭제되었습니다.", articleId);
        }

        log.info("세션 {} 가 {} 채팅방에서 퇴장하였습니다, 인원수 {}", sessionId, articleId, count);
        sendCountToClients(articleId, count);

    }

    /**
     * 현재 특정 뉴스에 연결된 사용자 세션 수를 반환한다.
     *
     * @param articleId 뉴스 식별자
     * @return 현재 채팅방에 연결된 세션 수
     * @author 이해창
     * @since 2025-05-24
     * @modified 2025-06-01 이해창
     * - 접속 세션 수를 즉시 발행 하여 구독자들이 세션수를 받을 수 있도록 수정
     */
    @Override
    public int getSessionCount(String articleId) {
        String roomKey = SESSION_KEY_PREFIX + articleId;
        Long count = redisTemplate.opsForSet().size(roomKey);
        sendCountToClients(articleId, count != null ? count.intValue() : 0);
        return count != null ? count.intValue() : 0;
    }

    /**
     * 세션이 존재하지 않으면 새로 생성한다.
     *
     * @param articleId 뉴스 식별자
     * @author 이해창
     * @since 2025-05-24
     */
//    private void createRoomIfNotExists(String articleId) {
//        if (!sessions.containsKey(articleId)) {
//            sessions.put(articleId, ConcurrentHashMap.newKeySet());
//        }
//    }

    private void sendCountToClients(String articleId, int count) {
        String destination = CHAT_TOPIC_PREFIX + articleId + ".count";
        ChatCountResponse response = new ChatCountResponse(count);
        wsTemplate.convertAndSend(destination, response);
    }
}
