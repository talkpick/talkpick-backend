package com.likelion.backendplus4.talkpick.backend.auth.application.service;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.in.AuthServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.RedisAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.SecurityPort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserJpaRepoPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignInDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.AuthUserMapper;
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
	public void signUp(SignUpDto dto) {
		AuthUser authUser = AuthUserMapper.toDomainByDto(dto);
		userJpaRepoPort.existsByAccountAndEmail(authUser.getAccount());

		String encodePassword = securityPort.encodePassword(authUser.getPassword());
		authUser.updateEncodedPassword(encodePassword);

		userJpaRepoPort.saveUser(authUser);
	}

	@Override
	public TokenDto signIn(SignInDto dto) {
		Authentication auth = securityPort.authenticate(dto.account(), dto.password());
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
		long expiration = securityPort.getExpiration(accessToken);
		String userId = securityPort.getUserId(accessToken);
		redisAuthPort.logoutTokens(accessToken, expiration, userId);
	}

	@Override
	public void deleteUser(Long id) {
		userJpaRepoPort.deleteUser(id);
	}
}
