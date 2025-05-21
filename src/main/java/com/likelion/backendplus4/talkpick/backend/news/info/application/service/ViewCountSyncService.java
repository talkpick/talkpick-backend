package com.likelion.backendplus4.talkpick.backend.news.info.application.service;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.ViewCountSyncUseCase;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.out.ViewCountSyncJobPort;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

/**
 * 조회수 동기화 작업을 처리하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ViewCountSyncService implements ViewCountSyncUseCase {

    private final ViewCountSyncJobPort viewCountSyncJobPort;

    @Override
    public void syncViewCounts(String requestor) {
        viewCountSyncJobPort.executeJob(requestor);
    }
}