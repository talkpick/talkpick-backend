package com.likelion.backendplus4.talkpick.backend.common.util.security;

import com.likelion.backendplus4.talkpick.backend.auth.exception.AuthException;
import com.likelion.backendplus4.talkpick.backend.auth.exception.error.AuthErrorCode;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.custom.user.CustomUserDetails;

/**
 * 로그인된 사용자 정보를 편리하게 조회하는 유틸 클래스입니다.
 *
 * @since 2025-05-12
 * @modified 2025-05-12
 */
public class LoginUserUtil {

	/**
	 * Principal 객체에서 사용자 ID를 Long으로 추출합니다.
	 * CustomUserDetails가 아니거나 번호 형식이 올바르지 않으면 AuthException을 던집니다.
	 *
	 * @param principal 인증된 사용자 정보
	 * @return 사용자 ID (Long)
	 * @throws AuthException 인증 정보 미존재 또는 ID 파싱 실패 시
	 * @author 박찬병
	 * @since 2025-05-12
	 * @modified 2025-05-13
	 */
	public static Long getPrincipalOrThrow(Object principal) {
		if (principal instanceof CustomUserDetails userDetails) {
			try {
				return Long.valueOf(userDetails.getUsername());
			} catch (NumberFormatException e) {
				throw new AuthException(AuthErrorCode.INVALID_USER_ID_FORMAT, e);
			}
		}
		throw new AuthException(AuthErrorCode.AUTHENTICATION_REQUIRED);
	}
}