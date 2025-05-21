package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.scheduler;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StreamOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatMessageDbPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.ChatMessage;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChatMessageStreamFlusher {

	private final RedisTemplate<String, String> redisTemplate;
	private final ChatMessageDbPort dbPort;
	private final ObjectMapper objectMapper;

	private static final String STREAM_KEY_PREFIX = "chat:stream:";
	private static final String GROUP = "chatGroup";
	private static final String CONSUMER = "flusher-instance";
	private static final int BATCH_SIZE = 100;

	/** flush 주기(ms). 없으면 기본 30 초 */
	@Value("${chat.flush.delay:30000}")
	private long flushDelayMillis;

	/** 애플리케이션 기동 시 모든 스트림에 Consumer Group을 생성한다. */
	@PostConstruct
	public void initGroup() {
		streamKeys().forEach(this::createGroupIfAbsent);
	}

	/**
	 * 주기적으로 Redis Stream에서 메시지를 꺼내 DB로 영구 저장한다.
	 * flushDelayMillis 값(기본 30 초)마다 실행된다.
	 */
	@Scheduled(fixedDelayString = "${chat.flush.delay:30000}")
	public void flush() {
		for (String key : streamKeys()) {
			List<MapRecord<String, String, String>> records = readBatch(key);
			if (records.isEmpty()) {
				continue;
			}

			List<ChatMessage> messages = convertToMessages(records);
			persist(messages);
			acknowledge(key, records);
		}
	}

	/** 스트림 키 목록을 조회한다. */
	private Set<String> streamKeys() {
		return redisTemplate.keys(STREAM_KEY_PREFIX + "*");
	}

	/**
	 * 해당 스트림에 Consumer Group이 없다면 생성한다.
	 *
	 * @param key 스트림 키
	 */
	private void createGroupIfAbsent(String key) {
		try {
			redisTemplate.opsForStream().createGroup(key, GROUP);
		} catch (Exception ignored) {
			// 이미 존재하거나 race condition일 수 있으므로 무시
		}
	}

	/**
	 * 지정한 스트림에서 최대 {@code BATCH_SIZE}개의 레코드를 읽어온다.
	 *
	 * @param key 스트림 키
	 * @return 읽어온 레코드(없으면 빈 리스트)
	 */
	private List<MapRecord<String, String, String>> readBatch(String key) {
		StreamOperations<String, String, String> ops = redisTemplate.opsForStream();
		List<MapRecord<String, String, String>> records = ops.read(
			Consumer.from(GROUP, CONSUMER),
			StreamReadOptions.empty()
				.count(BATCH_SIZE)
				.block(Duration.ofMillis(flushDelayMillis / 2)), // 대기 시간은 flush 주기의 절반
			StreamOffset.create(key, ReadOffset.lastConsumed())
		);
		return records != null ? records : Collections.emptyList();
	}

	/**
	 * 레코드 목록을 {@link ChatMessage} 객체로 변환한다.
	 *
	 * @param records Redis Stream 레코드
	 * @return 변환된 도메인 모델
	 * @throws RuntimeException JSON 변환 실패 시
	 */
	private List<ChatMessage> convertToMessages(List<MapRecord<String, String, String>> records) {
		List<ChatMessage> result = new ArrayList<>(records.size());
		for (MapRecord<String, String, String> record : records) {
			result.add(parseMessage(record));
		}
		return result;
	}

	/**
	 * 단일 레코드를 {@link ChatMessage}로 변환한다.
	 *
	 * @param record Redis Stream 레코드
	 * @return {@link ChatMessage}
	 */
	private ChatMessage parseMessage(MapRecord<String, String, String> record) {
		try {
			String json = record.getValue().get("payload");
			return objectMapper.readValue(json, ChatMessage.class);
		} catch (Exception e) {
			throw new RuntimeException("ChatMessage JSON 파싱 실패", e);
		}
	}

	/**
	 * 변환된 메시지를 DB에 저장한다.
	 *
	 * @param messages 저장할 메시지 목록
	 */
	private void persist(List<ChatMessage> messages) {
		dbPort.saveAll(messages);
	}

	/**
	 * 읽어들인 레코드를 ACK 하여 재처리를 방지한다.
	 *
	 * @param key     스트림 키
	 * @param records 처리 완료된 레코드
	 */
	private void acknowledge(String key, List<MapRecord<String, String, String>> records) {
		RecordId[] ids = records.stream()
			.map(MapRecord::getId)
			.toArray(RecordId[]::new);
		redisTemplate.opsForStream().acknowledge(key, GROUP, ids);
	}
}