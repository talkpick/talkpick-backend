package com.likelion.backendplus4.talkpick.backend.auth.application.port.in;

import com.likelion.backendplus4.talkpick.backend.auth.infrastructure.dto.JwtToken;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignInDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignUpDto;

public interface AuthServiceUseCase {

	void signUp(SignUpDto dto);

	JwtToken signIn(SignInDto dto);

	JwtToken refreshToken(String refreshToken);

	void logout(String accessToken);

	void deleteUser(Long id);

}
