package com.likelion.backendplus4.talkpick.backend.auth.application.port.in;

import com.likelion.backendplus4.talkpick.backend.auth.domain.model.AuthUser;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.TokenDto;

public interface AuthServiceUseCase {

	void signUp(AuthUser authUser);

	TokenDto signIn(String account, String password);

	TokenDto refreshToken(String refreshToken);

	void logout(String accessToken);

	void deleteUser(Long id);

}
