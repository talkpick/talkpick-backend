package com.likelion.backendplus4.talkpick.backend.chat.infrastructure.adapter.out;

import com.likelion.backendplus4.talkpick.backend.chat.application.port.out.ChatRankingPort;
import com.likelion.backendplus4.talkpick.backend.chat.domain.model.RoomRankDto;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@Component
@RequiredArgsConstructor
public class RedisChatRankingAdapter implements ChatRankingPort {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Redis에서 특정 카테고리의 최고 인기 뉴스를 조회합니다.
     * <p>
     * 1. 카테고리 패턴 문자열 생성
     * 2. Lua 스크립트 실행으로 원자적 조회
     * 3. 결과 파싱하여 RoomRankDto 객체 반환
     *
     * @param category 조회할 카테고리명
     * @return 카테고리별 최고 인기 채팅방 정보
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    @Override
    public RoomRankDto getTopNewsByCategory(String category) {
        String pattern = buildCategoryPattern(category);
        List<Object> result = executeTopNewsByCategoryScript(pattern);
        return parseTopNewsByCategoryResult(category, result);
    }

    /**
     * Redis에서 전체 카테고리의 최고 인기 뉴스를 조회합니다.
     * <p>
     * 1. 전체 카테고리 대상 Lua 스크립트 실행
     * 2. 모든 채팅방 중 최고 참여자 수 채팅방 조회
     * 3. 결과 파싱하여 RoomRankDto 객체 반환
     *
     * @return 전체 카테고리 중 최고 인기 채팅방 정보
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    @Override
    public RoomRankDto getTopNewsAll() {
        List<Object> result = executeTopNewsAllScript();
        return parseTopNewsAllResult(result);
    }

    /**
     * 카테고리별 Redis 키 패턴을 생성합니다.
     *
     * @param category 카테고리명
     * @return Redis 키 패턴 (예: "chat:room:politics:*")
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private String buildCategoryPattern(String category) {
        return "chat:room:" + category + ":*";
    }

    /**
     * 카테고리별 최고 인기 뉴스 조회 Lua 스크립트를 실행합니다.
     *
     * @param pattern Redis 키 패턴
     * @return 스크립트 실행 결과 리스트
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private List<Object> executeTopNewsByCategoryScript(String pattern) {
        DefaultRedisScript<List> script = createTopNewsByCategoryScript();
        return (List<Object>) redisTemplate.execute(script, Collections.emptyList(), pattern);
    }

    /**
     * 전체 카테고리 최고 인기 뉴스 조회 Lua 스크립트를 실행합니다.
     *
     * @return 스크립트 실행 결과 리스트
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private List<Object> executeTopNewsAllScript() {
        DefaultRedisScript<List> script = createTopNewsAllScript();
        return (List<Object>) redisTemplate.execute(script, Collections.emptyList());
    }

    /**
     * 카테고리별 최고 인기 뉴스 조회용 Redis Lua 스크립트를 생성합니다.
     *
     * @return 설정된 Lua 스크립트 객체
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private DefaultRedisScript<List> createTopNewsByCategoryScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/get-top-news-by-category.lua"));
        script.setResultType(List.class);
        return script;
    }

    /**
     * 전체 카테고리 최고 인기 뉴스 조회용 Redis Lua 스크립트를 생성합니다.
     *
     * @return 설정된 Lua 스크립트 객체
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private DefaultRedisScript<List> createTopNewsAllScript() {
        DefaultRedisScript<List> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("lua/get-top-news-all.lua"));
        script.setResultType(List.class);
        return script;
    }

    /**
     * 카테고리별 조회 결과를 RoomRankDto 객체로 파싱합니다.
     *
     * @param category 카테고리명
     * @param result   Redis 스크립트 실행 결과
     * @return 파싱된 RoomRankDto 객체, 빈 결과면 null
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private RoomRankDto parseTopNewsByCategoryResult(String category, List<Object> result) {
        if (isEmptyResult(result)) {
            return null;
        }

        String articleId = extractStringValue(result, 0);
        Integer count = extractIntValue(result, 1);

        return new RoomRankDto(category, articleId, count);
    }

    /**
     * 전체 카테고리 조회 결과를 RoomRankDto 객체로 파싱합니다.
     *
     * @param result Redis 스크립트 실행 결과
     * @return 파싱된 RoomRankDto 객체, 빈 결과면 null
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private RoomRankDto parseTopNewsAllResult(List<Object> result) {
        if (isEmptyResult(result)) {
            return null;
        }

        String category = extractStringValue(result, 0);
        String articleId = extractStringValue(result, 1);
        Integer count = extractIntValue(result, 2);

        return new RoomRankDto(category, articleId, count);
    }

    /**
     * 결과가 비어있는지 확인합니다.
     *
     * @param result 확인할 결과 리스트
     * @return 비어있으면 true, 그렇지 않으면 false
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private boolean isEmptyResult(List<Object> result) {
        return null == result || result.isEmpty();
    }

    /**
     * 결과 리스트에서 지정된 인덱스의 값을 문자열로 추출합니다.
     *
     * @param result 결과 리스트
     * @param index  추출할 인덱스
     * @return 문자열로 변환된 값
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private String extractStringValue(List<Object> result, int index) {
        return result.get(index).toString();
    }

    /**
     * 결과 리스트에서 지정된 인덱스의 값을 정수로 추출합니다.
     *
     * @param result 결과 리스트
     * @param index  추출할 인덱스
     * @return 정수로 변환된 값
     * @author 양병학
     * @since 2025-06-03 최초 작성
     */
    private Integer extractIntValue(List<Object> result, int index) {
        return Integer.parseInt(result.get(index).toString());
    }
}