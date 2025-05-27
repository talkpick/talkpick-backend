package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.jpa.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.cache.CaffeineCacheMetrics;
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

    public CacheConfig(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(Duration.ofHours(6))
                .recordStats());

        cacheManager.setCacheNames(List.of("popularNews"));

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