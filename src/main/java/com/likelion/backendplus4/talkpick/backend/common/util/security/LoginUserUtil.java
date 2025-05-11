package com.likelion.backendplus4.talkpick.backend.common.util.security;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.user.CustomUserDetails;

public class LoginUserUtil {

	/**
	 * 인증된 Principal 객체를 반환하거나 예외를 발생
	 *
	 * @param principal 인증된 사용자 정보
	 * @return userId 사용자 ID (Long)
	 * @throws AuthenticationCredentialsNotFoundException 인증되지 않은 경우
	 */
	public static Long getPrincipalOrThrow(Object principal) {
		if (principal instanceof CustomUserDetails) {
			try {
				// String 타입의 ID를 Long으로 변환
				return Long.valueOf(((CustomUserDetails) principal).getUsername());
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("ID 값이 Long으로 변환할 수 없습니다: " + ((CustomUserDetails) principal).getUsername());
			}
		}

		// 인증되지 않았거나 예상치 못한 객체 타입일 경우 예외 발생
		throw new AuthenticationCredentialsNotFoundException("사용자 인증 정보가 필요합니다.");
	}

}
