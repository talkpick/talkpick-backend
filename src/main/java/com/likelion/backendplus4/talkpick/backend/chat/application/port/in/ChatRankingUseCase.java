package com.likelion.backendplus4.talkpick.backend.chat.application.port.in;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.RoomRankDto;

public interface ChatRankingUseCase {
    RoomRankDto getTopNewsByCategory(String category);

    RoomRankDto getTopNewsAll();
}