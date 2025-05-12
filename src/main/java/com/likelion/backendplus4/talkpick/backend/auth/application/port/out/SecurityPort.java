package com.likelion.backendplus4.talkpick.backend.auth.application.port.out;


import org.springframework.security.core.Authentication;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo.TokenInfo;

public interface SecurityPort {
	/** 아이디·비밀번호로 인증 수행 */
	Authentication authenticate(String account, String rawPassword);

	/** 평문 비밀번호를 해시로 변환 */
	String encodePassword(String rawPassword);

	/** Authentication → 액세스·리프레시 토큰 발급 */
	TokenPair issueToken(Authentication authentication);

	/** 리프레시 토큰으로 액세스 토큰 재발급 */
	TokenPair refreshToken(String refreshToken);

	TokenInfo parseTokenInfo(String accessToken);

}
