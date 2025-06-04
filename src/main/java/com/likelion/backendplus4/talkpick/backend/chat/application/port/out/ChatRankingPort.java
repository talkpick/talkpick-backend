package com.likelion.backendplus4.talkpick.backend.chat.application.port.out;

import com.likelion.backendplus4.talkpick.backend.chat.domain.model.RoomRankDto;

public interface ChatRankingPort {
    RoomRankDto getTopNewsByCategory(String category);

    RoomRankDto getTopNewsAll();
}