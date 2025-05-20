package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.item;

import java.util.Iterator;
import java.util.Set;

import org.springframework.batch.item.ItemReader;
import org.springframework.data.redis.core.RedisTemplate;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;

/**
 * Redis에서 뉴스 조회수 데이터를 읽는 ItemReader 구현 클래스입니다.
 * 특정 패턴에 일치하는 모든 Redis 키를 대상으로 조회수 데이터를 가져옵니다.
 *
 * @since 2025-05-20
 */
public class ViewCountRedisReader implements ItemReader<ViewCountItem> {

    private static final String VIEW_COUNT_KEY_PREFIX = "news:viewCount:";

    private final RedisTemplate<String, String> redisTemplate;
    private final String keyPattern;
    private Iterator<String> keyIterator;

    /**
     * Redis 템플릿과 키 패턴을 받아 Reader를 초기화합니다.
     *
     * @param redisTemplate Redis 작업을 위한 템플릿
     * @param keyPattern 가져올 키 패턴 (예: "news:viewCount:*")
     */
    public ViewCountRedisReader(RedisTemplate<String, String> redisTemplate, String keyPattern) {
        this.redisTemplate = redisTemplate;
        this.keyPattern = keyPattern;
    }

    /**
     * Redis에서 다음 조회수 항목을 읽어옵니다.
     * 모든 항목을 처리한 경우 null을 반환합니다.
     *
     * @return 조회수 항목 또는 null (처리 완료 시)
     * @throws NewsInfoException Redis 조회 실패 또는 데이터 파싱 오류 시
     */
    @Override
    public ViewCountItem read() {
        try {
            if (keyIterator == null) {
                // 첫 호출 시 Redis에서 모든 키 가져오기
                Set<String> keys = redisTemplate.keys(keyPattern);
                if (keys == null) {
                    return null;
                }
                keyIterator = keys.iterator();
            }

            if (keyIterator.hasNext()) {
                String key = keyIterator.next();
                String newsId = key.substring(VIEW_COUNT_KEY_PREFIX.length());
                String countValue = redisTemplate.opsForValue().get(key);

                if (countValue != null) {
                    try {
                        Long viewCount = Long.parseLong(countValue);
                        return new ViewCountItem(newsId, viewCount);
                    } catch (NumberFormatException e) {
                        throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_INVALID_FORMAT, e);
                    }
                }
            }

            // 모든 항목을 처리했거나 더 이상 항목이 없으면 null 반환
            return null;
        } catch (NewsInfoException e) {
            throw e;
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_SYNC_FAILED, e);
        }
    }
}