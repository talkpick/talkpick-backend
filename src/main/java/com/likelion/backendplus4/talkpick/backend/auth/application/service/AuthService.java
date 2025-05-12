package com.likelion.backendplus4.talkpick.backend.auth.application.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.in.AuthServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.RedisAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.SecurityPort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserJpaRepoPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo.TokenInfo;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.TokenDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.mapper.TokenDtoMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthServiceUseCase {

	private final RedisAuthPort redisAuthPort;
	private final UserJpaRepoPort userJpaRepoPort;
	private final SecurityPort securityPort;

	@Override
	public void signUp(AuthUser authUser) {
		validateAccountNotExists(authUser.getAccount());
		encodedPassword(authUser);
		userJpaRepoPort.saveUser(authUser);
	}

	@Override
	public TokenDto signIn(String account, String password) {
		Authentication auth = securityPort.authenticate(account, password);
		TokenPair pair = securityPort.issueToken(auth);
		return TokenDtoMapper.toDto(pair);
	}

	@Override
	public TokenDto refreshToken(String refreshToken) {
		TokenPair tokenPair = securityPort.refreshToken(refreshToken);
		return TokenDtoMapper.toDto(tokenPair);
	}

	@Override
	public void logout(String accessToken) {
		TokenInfo tokenInfo = securityPort.parseTokenInfo(accessToken);
		performLogoutIfValid(accessToken, tokenInfo);
	}

	@Override
	public void deleteUser(Long id) {
		userJpaRepoPort.deleteUser(id);
	}

	private void validateAccountNotExists(String account) {
		userJpaRepoPort.existsByAccountAndEmail(account);
	}

	private void encodedPassword(AuthUser authUser) {
		String encoded = securityPort.encodePassword(authUser.getPassword());
		authUser.updateEncodedPassword(encoded);
	}

	/**
	 * 토큰이 만료되지 않은 경우에만 Redis에 블랙리스트로 등록합니다.
	 *
	 * @param rawToken   원본 액세스 토큰
	 * @param tokenInfo  파싱된 토큰 정보
	 */
	private void performLogoutIfValid(String rawToken, TokenInfo tokenInfo) {
		if (!tokenInfo.isExpired()) {
			redisAuthPort.logoutTokens(
				rawToken,
				tokenInfo.getExpirationMillis(),
				tokenInfo.getUserId()
			);
		}
	}
}
