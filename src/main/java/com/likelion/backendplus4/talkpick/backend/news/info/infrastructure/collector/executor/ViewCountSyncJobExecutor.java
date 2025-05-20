package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.executor;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * Quartz에 의해 트리거되는 Spring Batch Job 실행 클래스.
 * JobLauncher를 통해 뉴스 조회수 동기화 Job을 수동으로 실행하며,
 * 매 실행 시 고유한 JobParameters를 생성하여 중복 실행을 방지합니다.
 *
 * @since 2025-05-20
 */
@Component
@DisallowConcurrentExecution
@RequiredArgsConstructor
public class ViewCountSyncJobExecutor implements org.quartz.Job {
    private final JobLauncher jobLauncher;
    private final Job viewCountSyncJob;

    /**
     * Quartz 트리거에 의해 호출되는 메서드.
     * 내부적으로 Spring Batch Job을 실행하는 로직을 위임합니다.
     *
     * @param jobExecutionContext Quartz 실행 컨텍스트
     * @since 2025-05-20
     */
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        startSpringBatchJob();
    }

    /**
     * Spring Batch Job을 JobLauncher를 통해 실행합니다.
     * 각 실행마다 timestamp 파라미터를 부여하여 중복 실행을 방지합니다.
     * 예외 발생 시 NewsInfoException으로 변환하여 처리합니다.
     *
     * @since 2025-05-20
     */
    private void startSpringBatchJob() {
        JobParameters params = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(viewCountSyncJob, params);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_SYNC_FAILED, e);
        }
    }
}