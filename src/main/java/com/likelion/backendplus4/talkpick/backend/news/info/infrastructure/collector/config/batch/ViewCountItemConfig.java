package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.config.batch;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.item.ViewCountItem;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.item.ViewCountRedisReader;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.item.ViewCountProcessor;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.item.ViewCountDatabaseWriter;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.service.ViewCountUpdateService;
import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.tasklet.OldDataCleanupTasklet;

/**
 * Spring Batch Item 구성 클래스.
 * 조회수 동기화를 위한 Reader, Processor, Writer 및 Tasklet을 설정합니다.
 *
 * @since 2025-05-20
 * @modified 2025-05-23 ViewCountUpdateService 추가
 */
@Configuration
public class ViewCountItemConfig {

    private final RedisTemplate<String, String> redisTemplate;
    private final ViewCountUpdateService viewCountUpdateService;

    public ViewCountItemConfig(
        RedisTemplate<String, String> redisTemplate,
        ViewCountUpdateService viewCountUpdateService) {
        this.redisTemplate = redisTemplate;
        this.viewCountUpdateService = viewCountUpdateService;
    }

    /**
     * Redis에서 조회수 데이터를 읽는 Reader를 생성합니다.
     *
     * @return 조회수 데이터 Reader
     * @since 2025-05-20
     */
    @Bean
    public ItemReader<ViewCountItem> viewCountReader() {
        return new ViewCountRedisReader(redisTemplate, "news:viewCount:*");
    }

    /**
     * 조회수 데이터를 처리하는 Processor를 생성합니다.
     *
     * @return 조회수 데이터 Processor
     * @since 2025-05-20
     */
    @Bean
    public ItemProcessor<ViewCountItem, ViewCountItem> viewCountProcessor() {
        return new ViewCountProcessor();
    }

    /**
     * 처리된 조회수 데이터를 DB에 저장하는 Writer를 생성합니다.
     *
     * @return 조회수 데이터 Writer
     * @since 2025-05-20
     */
    @Bean
    public ItemWriter<ViewCountItem> viewCountWriter() {
        return new ViewCountDatabaseWriter(viewCountUpdateService);
    }

    /**
     * 오래된 조회수 데이터를 정리하는 Tasklet을 생성합니다.
     *
     * @return 데이터 정리 Tasklet
     * @since 2025-05-20
     */
    @Bean
    public OldDataCleanupTasklet oldDataCleanupTasklet() {
        return new OldDataCleanupTasklet(redisTemplate);
    }
}