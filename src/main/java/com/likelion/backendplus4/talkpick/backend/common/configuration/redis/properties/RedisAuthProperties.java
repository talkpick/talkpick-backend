package com.likelion.backendplus4.talkpick.backend.common.configuration.redis.properties;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Getter;
import lombok.Setter;

/**
 * auth.redis.* 설정 값을 보관하는 프로퍼티 클래스.
 *
 * @author 박찬병
 * @since 2025-05-20
 */
@Getter
@Configuration
@ConfigurationProperties(prefix = "auth.redis")
@Setter
public class RedisAuthProperties {

    private String refreshTokenKey;

    private String authoritiesKey;

    private String blacklistIndicator;

    private String emailPrefix;

    private Duration verifyEmailCodeTtl;

    private int refreshTokenExpirationDays;

    private final Field field = new Field();

    @Getter
    @Setter
    public static class Field {
        private String emailAuthCode;

        private String account;
    }
}
