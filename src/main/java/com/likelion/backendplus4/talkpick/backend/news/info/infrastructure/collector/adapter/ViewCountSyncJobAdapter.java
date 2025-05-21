package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.adapter;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.ViewCountSyncJobPort;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ViewCountSyncJobAdapter implements ViewCountSyncJobPort {

    private final JobLauncher jobLauncher;
    private final Job viewCountSyncJob;

    @Override
    public void executeJob(String requestor) {
        try {
            JobParameters params = createJobParameters(requestor);
            jobLauncher.run(viewCountSyncJob, params);
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_SYNC_FAILED, e);
        }
    }

    private JobParameters createJobParameters(String requestor) {
        return new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("requestedBy", requestor)
                .toJobParameters();
    }
}