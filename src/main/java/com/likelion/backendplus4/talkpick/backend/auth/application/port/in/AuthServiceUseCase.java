package com.likelion.backendplus4.talkpick.backend.auth.application.port.in;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignInDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.TokenDto;

public interface AuthServiceUseCase {

	void signUp(SignUpDto dto);

	TokenDto signIn(SignInDto dto);

	TokenDto refreshToken(String refreshToken);

	void logout(String accessToken);

	void deleteUser(Long id);

}
