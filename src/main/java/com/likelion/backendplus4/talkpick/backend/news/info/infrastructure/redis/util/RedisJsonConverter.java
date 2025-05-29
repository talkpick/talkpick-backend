package com.likelion.backendplus4.talkpick.backend.news.info.infrastructure.redis.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.NewsInfoException;
import com.likelion.backendplus4.talkpick.backend.news.info.exception.error.NewsInfoErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * Redis에서 사용하는 JSON 직렬화/역직렬화를 담당하는 유틸리티 클래스입니다.
 *
 * 객체와 JSON 문자열 간의 변환을 처리하며, 변환 실패 시 적절한 예외를 발생시킵니다.
 *
 * @author 양병학
 * @since 2025-05-27 최초 작성
 */
@Component
@RequiredArgsConstructor
public class RedisJsonConverter {

    private final ObjectMapper objectMapper;

    /**
     * 객체를 JSON 문자열로 변환합니다.
     *
     * @param object 변환할 객체
     * @return JSON 문자열
     * @throws NewsInfoException JSON 직렬화 실패 시
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    public String toJson(Object object) {
        return convertToJson(object);
    }

    /**
     * JSON 문자열을 지정된 타입의 객체로 변환합니다.
     *
     * @param jsonString  JSON 문자열
     * @param targetClass 변환할 대상 클래스
     * @param <T>         변환할 타입
     * @return 변환된 객체
     * @throws NewsInfoException JSON 역직렬화 실패 시
     * @author 양병학
     * @since 2025-05-27 최초 작성
     */
    public <T> T fromJson(String jsonString, Class<T> targetClass) {
        return convertFromJson(jsonString, targetClass);
    }

    /**
     * 객체를 JSON으로 직렬화합니다.
     *
     * @param object 변환할 객체
     * @return JSON 문자열
     * @throws NewsInfoException 직렬화 실패 시
     */
    private String convertToJson(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new NewsInfoException(NewsInfoErrorCode.CACHE_JSON_SERIALIZE_FAILED, e);
        }
    }

    /**
     * JSON을 객체로 역직렬화합니다.
     *
     * @param jsonString  JSON 문자열
     * @param targetClass 변환할 대상 클래스
     * @param <T>         변환할 타입
     * @return 변환된 객체
     * @throws NewsInfoException 역직렬화 실패 시
     */
    private <T> T convertFromJson(String jsonString, Class<T> targetClass) {
        try {
            return objectMapper.readValue(jsonString, targetClass);
        } catch (JsonProcessingException e) {
            throw new NewsInfoException(NewsInfoErrorCode.CACHE_JSON_DESERIALIZE_FAILED, e);
        }
    }
}