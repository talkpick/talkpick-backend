package com.likelion.backendplus4.talkpick.backend.common.util.code;

import java.security.SecureRandom;

/**
 * 인증 코드 등의 보안 코드 생성을 위한 유틸리티 클래스입니다.
 *
 * @since 2025-05-20
 */
public class CodeGenerator {

	private static final SecureRandom RNG = new SecureRandom();

	/**
	 * 6자리 숫자 형식의 인증 코드를 생성합니다.
	 *
	 * @return 6자리 숫자로 구성된 문자열 코드
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	public static String generateCode() {
		int num = RNG.nextInt(1_000_000);
		return String.format("%06d", num);
	}
}