package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.config.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Batch Job 설정 클래스.
 * 뉴스 조회수 데이터를 Redis에서 DB로 동기화하는 Batch Job을 정의합니다.
 *
 * 이 잡은 {@code viewCountSyncJob}이라는 이름으로 정의되며,
 * {@link Step} 객체는 외부에서 주입받아 사용합니다.
 *
 * 해당 Job은 Quartz를 통해 매일 자정에 주기적으로 실행됩니다.
 *
 * @since 2025-05-20
 */
@Configuration
public class ViewCountBatchConfig {
    private final String jobName = "viewCountSyncJob";
    private final JobRepository jobRepository;
    private final Step viewCountSyncStep;

    public ViewCountBatchConfig(JobRepository jobRepository, Step viewCountSyncStep) {
        this.jobRepository = jobRepository;
        this.viewCountSyncStep = viewCountSyncStep;
    }

    /**
     * 뉴스 조회수 동기화용 Spring Batch Job Bean을 생성합니다.
     * 조회수 동기화 Step을 실행하도록 구성합니다.
     *
     * @return 뉴스 조회수 동기화 배치 Job
     * @since 2025-05-20
     */
    @Bean
    public Job viewCountSyncJob() {
        return new JobBuilder(jobName, jobRepository)
                .start(viewCountSyncStep)
                .build();
    }
}