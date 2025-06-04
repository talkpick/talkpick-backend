package com.likelion.backendplus4.talkpick.backend.chat.domain.model;

public record RoomRankDto(
        String category,
        String articleId,
        int participantCount
) {
}