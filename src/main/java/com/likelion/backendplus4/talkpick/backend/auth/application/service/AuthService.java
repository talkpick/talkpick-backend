package com.likelion.backendplus4.talkpick.backend.auth.application.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.in.AuthServiceUseCase;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.RedisAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.UserAuthPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignInDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.JwtProvider;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.AuthUserMapper;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.TokenDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.mapper.TokenDtoMapper;

import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AuthService implements AuthServiceUseCase {

	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;
	private final RedisAuthPort redisAuthPort;
	private final UserAuthPort userAuthPort;
	private final JwtProvider jwtProvider;

	@Override
	@Transactional
	public void signUp(SignUpDto dto) {
		AuthUser authUser = AuthUserMapper.toDomainByDto(dto);

		userAuthPort.existsByAccountAndEmail(authUser.getAccount());

		String encodePassword = passwordEncoder.encode(authUser.getPassword());

		authUser.updateEncodedPassword(encodePassword);

		userAuthPort.saveUser(authUser);
	}

	@Override
	public TokenDto signIn(SignInDto signInDto) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(signInDto.account()
				, signInDto.password()
			)
		);
		// access, refresh 토큰 생성 후 반환
		TokenPair tokenPair = jwtProvider.generateToken(authentication);

		return TokenDtoMapper.toDto(tokenPair);
	}

	@Override
	public TokenDto refreshToken(String refreshToken) {
		TokenPair tokenPair = jwtProvider.refreshAccessToken(refreshToken);

		return TokenDtoMapper.toDto(tokenPair);
	}

	@Override
	public void logout(String accessToken) {
		// 남은 유효기간
		long expiration = jwtProvider.getExpiration(accessToken);
		// 회원 id
		String userId = jwtProvider.getUserIdFromToken(accessToken);
		// 남은 유효기간 동안 블랙리스트 처리
		redisAuthPort.logoutTokens(accessToken, expiration, userId);
	}

	@Override
	@Transactional
	public void deleteUser(Long id) {
		userAuthPort.deleteUser(id);
	}
}
