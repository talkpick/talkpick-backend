package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.collector.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "news.viewcount")
@Getter
@Setter
public class NewsViewCountProperties {

    /**
     * 유효한 뉴스 ID 접두사 목록 (쉼표로 구분): application.yml에서 관리
     */
    private String validPrefixes;
}