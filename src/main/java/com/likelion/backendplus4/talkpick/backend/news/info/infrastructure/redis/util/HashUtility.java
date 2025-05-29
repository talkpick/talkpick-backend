package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util;

import org.springframework.util.DigestUtils;

/**
 * 해시 계산 유틸리티 클래스
 */
public class HashUtility {

    /**
     * 문자열을 MD5 해시로 변환
     */
    public static String calculateMd5Hash(String input) {
        if (input == null || input.isEmpty()) {
            return "empty";
        }
        return DigestUtils.md5DigestAsHex(input.getBytes());
    }

    /**
     * 랭킹 데이터 기반 해시 계산
     */
    public static String calculateRankingHash(String top1Data) {
        return calculateMd5Hash(top1Data);
    }
}