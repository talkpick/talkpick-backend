package com.likelion.backendplus4.talkpick.backend.auth.application.port.out;

import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;

/**
 * Redis를 이용해 리프레시 토큰 및 로그아웃 블랙리스트를 관리하는 어댑터 구현체.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public interface AuthTokenStorePort {

    /**
     * RefreshToken 과 사용자 권한 정보를 저장합니다.
     *
     * 1. 기존 데이터 삭제
     * 2. 해시맵에 토큰 및 권한 정보 저장
     * 3. 만료 시간 설정
     *
     * @param userId       사용자 식별자
     * @param refreshToken 저장할 리프레시 토큰
     * @param roles        사용자 권한 문자열
     * @throws AuthException 저장 실패 시 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    void storeRefreshToken(String userId, String refreshToken, String roles);

    /**
     * 저장된 리프레시 토큰이 유효한지 확인합니다.
     *
     * 1. 토큰 조회
     * 2. null 검사
     * 3. 토큰 일치 여부 반환
     *
     * @param userId       사용자 식별자
     * @param refreshToken 클라이언트가 제출한 리프레시 토큰
     * @return true 면 유효, false 면 무효
     * @throws AuthException 조회 실패 시 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    boolean isValidRefreshToken(String userId, String refreshToken);

    /**
     * 액세스 토큰이 블랙리스트에 등록되었는지 여부를 확인합니다.
     *
     * 1. 키 존재 여부 조회
     * 2. 결과 반환
     *
     * @param accessToken 검사할 액세스 토큰
     * @return true 면 블랙리스트 상태
     * @throws AuthException 조회 실패 시 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    boolean isTokenBlacklisted(String accessToken);

    /**
     * 로그아웃 시 액세스 토큰을 블랙리스트에 추가하고 리프레시 토큰을 삭제합니다.
     *
     * 1. 블랙리스트에 액세스 토큰 저장
     * 2. 리프레시 토큰 키 삭제
     *
     * @param accessToken            로그아웃할 액세스 토큰
     * @param accessTokenExpiration  만료까지 남은 시간(밀리초)
     * @param userId                 사용자 식별자
     * @throws AuthException 처리 실패 시 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    void logoutTokens(String accessToken, long accessTokenExpiration, String userId);

    /**
     * 사용자 권한 정보를 조회합니다.
     *
     * 1. 해시맵에서 AUTHORITIES_KEY 조회
     * 2. 결과 반환
     *
     * @param userId 사용자 식별자
     * @return 권한 문자열
     * @throws AuthException 조회 실패 시 발생
     * @author 박찬병
     * @since 2025-05-12
     * @modified 2025-05-12
     */
    String getAuthorities(String userId);

    /**
     * 이메일 인증 코드를 Redis에 저장합니다.
     *
     * @param email 인증 코드를 저장할 이메일 주소
     * @param emailAuthCode 저장할 인증 코드
     * @author 박찬병
     * @since 2025-05-20
     */
	void saveVerifyCode(String email, String emailAuthCode);

    /**
     * 이메일 인증 코드를 검증합니다.
     *
     * Redis에서 저장된 인증 코드가 존재하는지 확인하고,
     * 사용자가 입력한 코드와 일치하는지 검증합니다.
     * 검증에 성공하면 해당 인증 코드를 Redis에서 삭제합니다.
     *
     * @param email 인증할 이메일 주소
     * @param code 사용자가 입력한 인증 코드
     * @throws AuthException 인증 실패 시 예외 발생
     * @author 박찬병
     * @since 2025-05-20
     */
    void verifyCode(String email, String code);
}
