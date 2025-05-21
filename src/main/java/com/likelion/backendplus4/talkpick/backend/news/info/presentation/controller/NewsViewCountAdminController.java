package com.likelion.backendplus4.talkpick.backend.news.info.presentation.controller;

import static com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse.success;

import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.ViewCountSyncUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.likelion.backendplus4.talkpick.backend.common.annotation.security.LoginUser;
import com.likelion.backendplus4.talkpick.backend.common.response.ApiResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.service.ViewCountSyncService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 뉴스 조회수 관련 관리 기능을 제공하는 컨트롤러입니다.
 * 관리자 권한이 필요한 기능들을 포함합니다.
 *
 * @since 2025-05-20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/news/viewcount")
public class NewsViewCountAdminController {

    private final ViewCountSyncUseCase viewCountSyncUseCase;

    /**
     * Redis의 조회수 데이터를 수동으로 DB에 동기화합니다.
     * 관리자 권한을 가진 사용자만 호출할 수 있습니다.
     *
     * @param userId 요청한 관리자 ID
     * @return 성공 응답
     * @since 2025-05-20 최초 작성
     * @author 양병학
     */
    @PostMapping("/sync")
    public ResponseEntity<ApiResponse<String>> syncViewCountManually(@LoginUser Long userId) {
        String requestor = resolveRequestor(userId);
        viewCountSyncUseCase.syncViewCounts(requestor);
        return success("조회수 동기화가 시작되었습니다.");
    }

    private String resolveRequestor(Long userId) {
        return (userId != null) ? userId.toString() : "anonymous";
    }
}