package com.likelion.backendplus4.talkpick.backend.common.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.time.Duration;
import java.util.List;

@Configuration
@EnableCaching
public class CacheConfig {

    private final MeterRegistry meterRegistry;

    @Value("${cache.maximum-size:100}")
    private int maximumSize;

    @Value("${cache.expire-after-write:6h}")
    private Duration expireAfterWrite;

    public CacheConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite)
                .recordStats());

        cacheManager.setCacheNames(List.of(
                "popularNews",
                "chatTopNews",
                "newsMetadata"
        ));

        return cacheManager;
    }

    @EventListener
    public void bindCacheMetrics(ApplicationReadyEvent event) {
        CacheManager cacheManager = event.getApplicationContext().getBean(CacheManager.class);

        cacheManager.getCacheNames().forEach(cacheName -> {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache instanceof CaffeineCache) {
                CaffeineCache caffeineCache = (CaffeineCache) cache;
                CaffeineCacheMetrics.monitor(meterRegistry, caffeineCache.getNativeCache(), cacheName);
            }
        });

    }
}