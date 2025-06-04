package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatRankingUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.RoomRankDto;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.docs.ChatPopularRestControllerDocs;
import com.likelion.backendplus4.talkpick.backend.chat.presentation.validation.CategoryConstraint;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class ChatPopularRestController implements ChatPopularRestControllerDocs{

    private final ChatRankingUseCase chatRankingUseCase;

    /**
     * 특정 카테고리에서 가장 인기있는 뉴스 채팅방을 조회하는 API입니다.
     *
     * 1. 카테고리 유효성 검증 (CategoryType enum 기반)
     * 2. 해당 카테고리의 최고 인기 뉴스 조회
     * 3. 결과 반환 (없으면 null)
     *
     * @param category 조회할 뉴스 카테고리
     * @return 해당 카테고리의 최고 인기 채팅방 정보
     * @throws jakarta.validation.ConstraintViolationException 카테고리가 유효하지 않은 경우
     * @since 2025-06-03 최초 작성
     * @author 양병학
     * @modify 2025-06-03 양병학
     *  - @Valid에서 @CategoryConstraint로 변경
     */
    @Override
    @GetMapping("/public/chat/top-news/{category}")
    public RoomRankDto getTopNews(@PathVariable @CategoryConstraint String category) {
        return chatRankingUseCase.getTopNewsByCategory(category);
    }

    /**
     * 전체 카테고리에서 가장 인기있는 뉴스 채팅방을 조회하는 API입니다.
     *
     * 1. 모든 카테고리의 채팅방 데이터 조회
     * 2. 전체에서 가장 높은 참여자 수를 가진 채팅방 반환
     *
     * @return 전체 카테고리 중 최고 인기 채팅방 정보
     * @since 2025-06-03 최초 작성
     * @author 양병학
     */
    @Override
    @GetMapping("/public/chat/top-news")
    public RoomRankDto getTopNewsAll() {
        return chatRankingUseCase.getTopNewsAll();
    }
}