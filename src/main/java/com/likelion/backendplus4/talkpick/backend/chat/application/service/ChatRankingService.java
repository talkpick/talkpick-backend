package com.likelion.backendplus4.talkpick.backend.chat.application.service;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.in.ChatRankingUseCase;
import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatRankingPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.RoomRankDto;
import com.likelion.backendplus4.talkpick.backend.news.info.application.dto.PopularNewsResponse;
import com.likelion.backendplus4.talkpick.backend.news.info.application.port.in.PopularNewsUseCase;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRankingService implements ChatRankingUseCase {

    private final ChatRankingPort chatRankingPort;
    private final PopularNewsUseCase popularNewsUseCase;

    /**
     * 특정 카테고리에서 가장 인기있는 뉴스 채팅방을 조회합니다.
     * <p>
     * 1. 카테고리별 채팅방 키 패턴 생성
     * 2. Redis Lua 스크립트를 통한 원자적 조회 수행
     * 3. 가장 높은 참여자 수를 가진 채팅방 반환
     *
     * @param category 조회할 뉴스 카테고리 (예: "politics", "sports", "entertainment")
     * @return 해당 카테고리의 최고 인기 채팅방 정보, 없으면 null
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    @Override
    @Cacheable(value = "chatTopNews", key = "#category")
    public PopularNewsResponse getTopNewsByCategory(String category) {

        RoomRankDto rankDto = chatRankingPort.getTopNewsByCategory(category);
        PopularNewsResponse response = (null == rankDto)
                ? null
                : popularNewsUseCase.getTopNewsByCategory(rankDto.category());

        return response;
    }

    /**
     * 전체 카테고리에서 가장 인기있는 뉴스 채팅방을 조회합니다.
     * <p>
     * 1. 모든 카테고리의 채팅방 데이터 조회
     * 2. Redis Lua 스크립트를 통한 전체 랭킹 계산
     * 3. 전체에서 가장 높은 참여자 수를 가진 채팅방 반환
     *
     * @return 전체 카테고리 중 최고 인기 채팅방 정보, 없으면 null
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    @Override
    @Cacheable(value = "chatTopNews", key = "'all'")
    public PopularNewsResponse getTopNewsAll() {

        RoomRankDto rankDto = chatRankingPort.getTopNewsAll();
        PopularNewsResponse response = (null == rankDto)
                ? null
                : popularNewsUseCase.getTopNewsByCategory(rankDto.category());

        return response;
    }
}