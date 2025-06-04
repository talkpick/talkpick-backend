//package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter;
//
//import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatSessionPort;
//import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response.ChatCountResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.messaging.simp.SimpMessagingTemplate;
//import org.springframework.stereotype.Component;
//
//import java.util.Collections;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 인메모리 기반으로 채팅 세션을 관리하는 어댑터 클래스
// * <p>
// * 채팅방 별로 연결된 세션을 관리하며, 세션 입장/퇴장 시 사용자 수를 실시간으로 브로드캐스팅한다.
// * <p>
// * 내부적으로 두 개의 맵을 사용하여 세션을 관리한다:<br/>
// * - sessions: 뉴스(articleId) 기준으로 연결된 세션 ID들을 저장하여 채팅방 인원수를 추적<br/>
// * - reverse: 세션 ID 기준으로 어떤 뉴스에 연결되어 있는지 역추적하여 빠르게 제거 처리 가능
// *
// * @since 2025-05-24
// */
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class InMemoryChatSessionAdapter implements ChatSessionPort {
//
//    private final ConcurrentHashMap<String, Set<String>> sessions = new ConcurrentHashMap<>(); // 채팅 방 별 세션관리 맵
//    private final ConcurrentHashMap<String, String> reverse = new ConcurrentHashMap<>(); // 세션이 속한 채팅방 관리 맵
//    private final SimpMessagingTemplate wsTemplate;
//
//    private static final String CHAT_TOPIC_PREFIX = "/topic/chat.";
//
//    /**
//     * 사용자가 특정 뉴스 채팅방에 입장했을 때 세션을 등록하고,
//     * 전체 접속 인원 수를 해당 뉴스의 .count 토픽으로 실시간 전송한다.
//     *
//     * @param articleId 뉴스 식별자
//     * @param sessionId 사용자의 웹소켓 세션 ID
//     * @author 이해창
//     * @since 2025-05-24
//     *
//     */
//    @Override
//    public void addSession(String articleId, String category ,String sessionId) {
//        createRoomIfNotExists(articleId);
//        sessions.get(articleId).add(sessionId);
//        reverse.put(sessionId, articleId);
//        int count = getSessionCount(articleId);
//
//        log.info("세션 {} 가 {} 채팅방에 입장하였습니다, 인원수 {}", sessionId, articleId, count);
//
//        String destination = CHAT_TOPIC_PREFIX + articleId + ".count";
//        ChatCountResponse response = new ChatCountResponse(count);
//        wsTemplate.convertAndSend(destination, response);
//    }
//
//    /**
//     * 사용자가 채팅방에서 퇴장하거나 연결이 종료되었을 때 세션을 제거하고,
//     * 남은 접속 인원 수를 .count 토픽으로 실시간 전송한다.
//     * 모든 사용자가 퇴장하면 해당 채팅방에 대한 메모리 정보도 제거한다.
//     *
//     * @param sessionId 사용자의 웹소켓 세션 ID
//     * @author 이해창
//     * @since 2025-05-24
//     */
//    @Override
//    public void removeSession(String sessionId) {
//        String articleId = reverse.get(sessionId);
//        if (articleId == null) {
//            return;
//        }
//        reverse.remove(sessionId);
//        Set<String> roomSessions = sessions.get(articleId);
//        if (roomSessions != null) {
//            roomSessions.remove(sessionId);
//            int count = getSessionCount(articleId);
//            if (count == 0) {
//                sessions.remove(articleId);
//                log.info("채팅방 {} 가 비어 있어 채팅방 맵에서 삭제되었습니다.", articleId);
//            }
//            log.info("세션 {} 가 {} 채팅방에서 퇴장하였습니다, 인원수 {}", sessionId, articleId, count);
//            String destination = CHAT_TOPIC_PREFIX + articleId + ".count";
//            ChatCountResponse response = new ChatCountResponse(count);
//            wsTemplate.convertAndSend(destination, response);
//        }
//    }
//
//    /**
//     * 현재 특정 뉴스에 연결된 사용자 세션 수를 반환한다.
//     *
//     * @param articleId 뉴스 식별자
//     * @return 현재 채팅방에 연결된 세션 수
//     * @author 이해창
//     * @since 2025-05-24
//     */
//    @Override
//    public int getSessionCount(String articleId) {
//        return sessions.getOrDefault(articleId, Collections.emptySet()).size();
//    }
//
//    /**
//     * 세션이 존재하지 않으면 새로 생성한다.
//     *
//     * @param articleId 뉴스 식별자
//     * @author 이해창
//     * @since 2025-05-24
//     */
//    private void createRoomIfNotExists(String articleId) {
//        if (!sessions.containsKey(articleId)) {
//            sessions.put(articleId, ConcurrentHashMap.newKeySet());
//        }
//    }
//}
