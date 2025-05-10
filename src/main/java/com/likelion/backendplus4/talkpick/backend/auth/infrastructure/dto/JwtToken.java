package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.dto;

import lombok.Builder;

@Builder
public record JwtToken(
        String accessToken,
        String refreshToken
) {

    public static JwtToken of(String accessToken, String refreshToken) {
        return JwtToken.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
