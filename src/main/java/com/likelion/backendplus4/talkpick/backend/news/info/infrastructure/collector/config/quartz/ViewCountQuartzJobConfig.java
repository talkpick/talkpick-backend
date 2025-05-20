package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.config.quartz;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.executor.ViewCountSyncJobExecutor;

/**
 * 뉴스 조회수 동기화를 위한 Quartz Job 설정 클래스.
 * 매일 자정에 Redis에 저장된 조회수 데이터를 DB에 동기화하는 작업을 실행합니다.
 *
 * @since 2025-05-19
 */
@Configuration
public class ViewCountQuartzJobConfig {
	private final String viewCountSyncJobDetailName = "viewCountSyncJobDetail";

	/**
	 * 조회수 동기화 Quartz JobDetail 빈 등록.
	 * Job 클래스는 {@link ViewCountSyncJobExecutor}이며 다음과 같은 설정을 포함합니다:
	 * - withIdentity: Scheduler 내에서 이 Job을 고유하게 식별하기 위한 이름 지정
	 * - storeDurably: Trigger가 없더라도 Scheduler에 등록된 상태로 유지되도록 설정
	 *
	 * @return 조회수 동기화 작업용 JobDetail 객체
	 * @since 2025-05-19
	 */
	@Bean
	public JobDetail viewCountSyncJobDetail() {
		return JobBuilder.newJob(ViewCountSyncJobExecutor.class)
			.withIdentity(viewCountSyncJobDetailName)
			.storeDurably()
			.build();
	}
}