package com.likelion.backendplus4.talkpick.backend.news.info.exception.error;

import org.springframework.http.HttpStatus;

import com.likelion.backendplus4.talkpick.backend.common.exception.error.ErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 에러 코드 인터페이스 각 에러 항목에 대한 HTTP 상태, 에러 번호, 메시지를 제공한다.
 * A[BB][CCC]
 * A (1자리) : 에러 심각도 (1~5)
 * 1: 클라이언트 오류
 * 2: 인증 관련 오류
 * 3: 사용자 관련 오류
 * 4: 서버 오류
 * 5: 시스템 오류
 *
 * BB (2자리) : 도메인 코드
 * 10: 사용자 관련 (ex: USER_NOT_FOUND)
 * 20: 인증 관련 (ex: AUTHORIZATION_FAILED)
 * 30: DB 관련 오류 (ex: DB_CONNECTION_FAILED)
 * 40: API 관련 오류 (ex: API_TIMEOUT)
 * 50: 시스템 오류 (ex: INTERNAL_SERVER_ERROR)
 * 60: 뉴스 도메인 (ex: NEWS_NOT_FOUND)
 *
 * CCC (3자리) : 세부 오류 순번
 * 001: 첫 번째 오류
 * 002: 두 번째 오류
 * 003: 세 번째 오류, 등등
 *
 * 뉴스 정보 처리 과정에서 발생할 수 있는 예외 상황을 정의한 열거형 클래스입니다.
 * 각 예외는 HTTP 상태 코드, 고유 코드 번호, 메시지를 포함합니다.
 *
 * @since 2025-05-14
 * @modified 2025-05-29
 */
@Getter
@RequiredArgsConstructor
public enum NewsInfoErrorCode implements ErrorCode {
	  NEWS_NOT_FOUND(HttpStatus.NOT_FOUND, 160001, "요청하신 뉴스 정보를 찾을 수 없습니다."),
	  SCRAP_VALIDATION_FAILED(HttpStatus.BAD_REQUEST, 160002, "스크랩 요청 데이터가 유효하지 않습니다."),

	  NON_UNIQUE_NEWS_INFO(HttpStatus.INTERNAL_SERVER_ERROR, 450001, "고유한 뉴스 ID 값이 아닙니다"),
	  NEWS_INFO_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, 450002, "뉴스 정보를 찾을수 없습니다"),
	  NEWS_CATEGORY_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR, 450003, "지원하지 않는 카테고리 입니다"),
	  VIEW_COUNT_INVALID_FORMAT(HttpStatus.BAD_REQUEST, 450004, "조회수 값의 형식이 올바르지 않습니다"),
	  VIEW_COUNT_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450005, "조회수 업데이트 처리 중 오류가 발생했습니다"),
	  VIEW_COUNT_CLEANUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450006, "오래된 조회수 데이터 정리 중 오류가 발생했습니다"),
	  VIEW_COUNT_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450014, "조회수 동기화 처리 중 오류가 발생했습니다"),

	  SCRAP_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 460001, "스크랩 저장 처리 중 오류가 발생했습니다.");
    VIEW_COUNT_INVALID_FORMAT(HttpStatus.BAD_REQUEST, 150003, "조회수 값의 형식이 올바르지 않습니다"),
    VIEW_COUNT_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450004, "조회수 업데이트 처리 중 오류가 발생했습니다"),
    VIEW_COUNT_CLEANUP_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450005, "오래된 조회수 데이터 정리 중 오류가 발생했습니다"),
    VIEW_COUNT_SYNC_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450006, "조회수 동기화 처리 중 오류가 발생했습니다"),
    VIEW_COUNT_REDIS_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450007, "Redis에 조회수 저장 중 오류가 발생했습니다"),
    VIEW_COUNT_REDIS_RETRIEVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450008, "Redis에서 조회수 조회 중 오류가 발생했습니다"),
    VIEW_COUNT_DB_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 430009, "데이터베이스에서 조회수 조회 중 오류가 발생했습니다"),
    VIEW_COUNT_HISTORY_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 430010, "조회 이력 저장 중 오류가 발생했습니다"),
    VIEW_COUNT_HISTORY_RETRIEVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 430011, "조회 이력 조회 중 오류가 발생했습니다"),

    POPULAR_NEWS_TOP1_NOT_FOUND(HttpStatus.NOT_FOUND, 150012, "카테고리별 1위 뉴스 데이터를 찾을 수 없습니다"),
    RANKING_HASH_CALCULATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450013, "순위 해시값 계산 중 오류가 발생했습니다"),
    RANKING_HASH_RETRIEVAL_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450014, "저장된 순위 해시값 조회 중 오류가 발생했습니다"),
    RANKING_HASH_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 430015, "순위 해시값 저장 중 오류가 발생했습니다"),
    POPULAR_NEWS_CACHE_EVICTION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450016, "인기뉴스 캐시 무효화 처리 중 오류가 발생했습니다"),
    POPULAR_NEWS_CATEGORY_PROCESSING_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450017, "카테고리별 인기뉴스 처리 중 오류가 발생했습니다"),

    RANKING_SCORE_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450018, "랭킹 점수 업데이트 중 오류가 발생했습니다"),
    RANKING_TOP1_QUERY_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 430019, "Top1 랭킹 조회 중 오류가 발생했습니다"),

    CACHE_JSON_SERIALIZE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450020, "JSON 직렬화 처리 중 오류가 발생했습니다"),
    CACHE_JSON_DESERIALIZE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450021, "JSON 역직렬화 처리 중 오류가 발생했습니다"),
    CACHE_SAVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450022, "캐시 저장 중 오류가 발생했습니다"),
    CACHE_RETRIEVE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, 450023, "캐시 조회 중 오류가 발생했습니다");

    private final HttpStatus status;
    private final int code;
    private final String message;

    /**
     * 예외에 대응하는 HTTP 상태 코드를 반환합니다.
     *
     * @author 함예정
     * @since 2025-05-14
     */
    @Override
    public HttpStatus httpStatus() {
        return status;
    }

    /**
     * 예외에 대한 고유 코드 번호를 반환합니다.
     *
     * @return 예외 코드 번호
     * @author 함예정
     * @since 2025-05-14
     */
    @Override
    public int codeNumber() {
        return code;
    }

    /**
     * 예외 메시지를 반환합니다.
     *
     * @return 예외 메시지
     * @author 함예정
     * @since 2025-05-14
     */
    @Override
    public String message() {
        return message;
    }
}