package com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.adapter.out;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.likelion.backendplus4.talkpick.backend.auth.application.port.out.SecurityPort;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.vo.TokenInfo;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.security.JwtProvider;
import com.likelion.backendplus4.talkpick.backend.auth.domain.model.TokenPair;
import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.support.mapper.TokenVoMapper;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SecurityAdapter implements SecurityPort {
	private final AuthenticationManager authManager;
	private final PasswordEncoder encoder;
	private final JwtProvider jwtProvider;

	@Override
	public Authentication authenticate(String account, String rawPassword) {
		return authManager.authenticate(
			new UsernamePasswordAuthenticationToken(account, rawPassword)
		);
	}

	@Override
	public String encodePassword(String rawPassword) {
		return encoder.encode(rawPassword);
	}

	@Override
	public TokenPair issueToken(Authentication authentication) {
		return jwtProvider.generateToken(authentication);
	}

	@Override
	public TokenPair refreshToken(String refreshToken) {
		return jwtProvider.refreshAccessToken(refreshToken);
	}

	@Override
	public TokenInfo parseTokenInfo(String accessToken) {
		long expiration = jwtProvider.getExpiration(accessToken);
		String userId = jwtProvider.getUserIdFromToken(accessToken);

		return TokenVoMapper.toVo(expiration, userId);
	}

}