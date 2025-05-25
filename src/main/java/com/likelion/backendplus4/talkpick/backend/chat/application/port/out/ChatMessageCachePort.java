package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import java.util.List;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

public interface ChatMessageCachePort {

	/**
	 * Redis 리스트에 채팅 메시지를 저장하고, 지정된 최대 캐시 크기를 유지하며 TTL을 설정합니다.
	 * 또한, 최대 크기를 초과하는 경우 다음 페이지 존재 여부 플래그를 갱신합니다.
	 *
	 * @param message       캐싱할 채팅 메시지 도메인 객체
	 * @param maxCacheSize  캐시할 최대 메시지 개수
	 * @author 박찬병
	 * @since 2025-05-25
	 */
	void cache(ChatMessage message, int maxCacheSize);

	/**
	 * 최근 캐싱된 채팅 메시지를 조회한다.
	 *
	 * @param articleId 채팅방 식별자
	 * @return 최근 채팅 메시지 목록
	 * @author 박찬병
	 * @since 2025-05-22
	 */
	List<ChatMessage> getRecentMessages(String articleId, int maxCacheSize);

	/**
	 * 주어진 기사 ID로 전달받은 최신 채팅 메시지 리스트를 Redis 캐시에 저장합니다.
	 * 캐시에 초기 데이터가 없으면 전체 메시지를 푸시하고, 이미 데이터가 있으면 기존 뒤에 추가합니다.
	 * 저장 후에는 다음 페이지 존재 여부 플래그를 업데이트합니다.
	 *
	 * @param articleId      게시글 식별자
	 * @param recentMessages 캐시에 저장할 최근 채팅 메시지 리스트
	 * @param hasNext        추가 메시지 존재 여부 플래그
	 * @author 박찬병
	 * @since 2025-05-25
	 */
	void cacheMessages(String articleId, List<ChatMessage> recentMessages, boolean hasNext);

	/**
	 * Redis에서 기사 ID에 대한 추가 메시지 존재 여부 플래그를 조회합니다.
	 *
	 * @param articleId 게시글 식별자
	 * @return 추가 메시지가 더 있는지 여부
	 * @author 박찬병
	 * @since 2025-05-25
	 */
	boolean getHasNextFlag(String articleId);
}
