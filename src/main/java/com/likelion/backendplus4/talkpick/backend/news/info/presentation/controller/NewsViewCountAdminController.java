package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.success;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.annotation.security.LoginUser;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;

import lombok.RequiredArgsConstructor;

/**
 * 뉴스 조회수 관련 관리 기능을 제공하는 컨트롤러입니다.
 * 관리자 권한이 필요한 기능들을 포함합니다.
 *
 * @since 2025-05-20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/news/viewcount")
public class NewsViewCountAdminController {

    private final JobLauncher jobLauncher;
    private final Job viewCountSyncJob;

    /**
     * Redis의 조회수 데이터를 수동으로 DB에 동기화합니다.
     * 관리자 권한을 가진 사용자만 호출할 수 있습니다.
     *
     * @return 성공 응답
     * @throws NewsInfoException 동기화 실패 시
     */
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<String>> syncViewCountManually(@LoginUser String userId) {
        try {
            JobParameters params = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .addString("requestedBy", userId)
                    .toJobParameters();

            // Spring Batch Job 실행
            jobLauncher.run(viewCountSyncJob, params);

            return success("조회수 동기화가 시작되었습니다.");
        } catch (Exception e) {
            throw new NewsInfoException(NewsInfoErrorCode.VIEW_COUNT_SYNC_FAILED, e);
        }
    }
}