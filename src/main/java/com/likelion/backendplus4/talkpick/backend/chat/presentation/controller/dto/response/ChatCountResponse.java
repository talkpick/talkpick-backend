package com.likelion.backendplus4.talkpick.backend.chat.presentation.controller.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatCountResponse {
    private int count;

    public ChatCountResponse(int count) {
        this.count = count;
    }
}
