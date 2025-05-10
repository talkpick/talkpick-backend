package com.likelion.backendplus4.talkpick.backend.auth.application.port.out;

import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.dto.RefreshTokenInfoDto;
import java.util.HashMap;

public interface RedisAuthPort {

    void storeRefreshToken(RefreshTokenInfoDto tokenData);

    boolean isValidRefreshToken(String userId, String refreshToken);

    void logoutTokens(String accessToken, long accessTokenExpiration, String userId);

    String getAuthorities(String userId);

}
