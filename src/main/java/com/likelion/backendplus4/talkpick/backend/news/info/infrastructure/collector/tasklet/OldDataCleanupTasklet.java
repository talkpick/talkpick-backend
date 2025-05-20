package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.tasklet;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.redis.core.RedisTemplate;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;

/**
 * 오래된 조회수 데이터를 정리하는 Tasklet 구현 클래스입니다.
 * 정리 정책에 따라 Redis의 오래된 데이터를 삭제합니다.
 *
 * @since 2025-05-20
 */
public class OldDataCleanupTasklet implements Tasklet {

    private static final String VIEW_COUNT_PATTERN = "news:viewCount:*";
    private static final int DATA_RETENTION_DAYS = 30;
    private static final int RECENT_DATA_DAYS = 7;
    private static final long MIN_VIEW_COUNT_THRESHOLD = 1000L;

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis 템플릿을 받아 Tasklet을 초기화합니다.
     *
     * @param redisTemplate Redis 작업을 위한 템플릿
     */
    public OldDataCleanupTasklet(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Tasklet 실행 메서드입니다.
     * Redis의 조회수 데이터를 정리 정책에 따라 관리합니다.
     *
     * @param contribution Step 실행 정보
     * @param chunkContext Chunk 실행 컨텍스트
     * @return 반복 상태 (FINISHED: 작업 완료)
     * @throws NewsInfoException 정리 작업 실패 시
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        try {
            Set<String> keys = redisTemplate.keys(VIEW_COUNT_PATTERN);

            if (keys == null || keys.isEmpty()) {
                return RepeatStatus.FINISHED;
            }

            for (String key : keys) {
                processKey(key);
            }

            return RepeatStatus.FINISHED;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_CLEANUP_FAILED, e);
        }
    }

    /**
     * 개별 키에 대한 정리 처리를 수행합니다.
     *
     * @param key 처리할 Redis 키
     */
    private void processKey(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.DAYS);
        if (ttl == null || ttl < 0) {
            return;
        }

        String countValue = redisTemplate.opsForValue().get(key);
        if (countValue == null) {
            return;
        }

        try {
            Long viewCount = Long.parseLong(countValue);
            Long remainingDays = DATA_RETENTION_DAYS - ttl;

            if (remainingDays >= DATA_RETENTION_DAYS) {
                redisTemplate.delete(key);
            } else if (remainingDays >= RECENT_DATA_DAYS && viewCount < MIN_VIEW_COUNT_THRESHOLD) {
                redisTemplate.delete(key);
            }
        } catch (NumberFormatException e) {
            // 숫자 변환 실패 - 무시하고 계속 진행
        }
    }
}