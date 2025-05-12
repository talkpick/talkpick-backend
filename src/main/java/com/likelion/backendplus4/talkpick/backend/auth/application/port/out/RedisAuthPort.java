package com.likelion.backendplus4.talkpick.backend.auth.application.port.out;


public interface RedisAuthPort {

    void storeRefreshToken(String userId, String refreshToken, String roles);

    boolean isValidRefreshToken(String userId, String refreshToken);

    void logoutTokens(String accessToken, long accessTokenExpiration, String userId);

    String getAuthorities(String userId);

    boolean isTokenBlacklisted(String accessToken);

}
