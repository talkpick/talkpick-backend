package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import org.springframework.stereotype.Component;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.SignInDto;

/**
 * SignInDto에 담긴 로그인 요청 정보를 검증하는 Validator.
 * 공통 계정·비밀번호 검증만 수행합니다.
 *
 * @author 박찬병
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Component
public class SignInDtoValidator extends AbstractAuthValidator<SignInDto> {

	public SignInDtoValidator() {
		super(SignInDto.class);
	}

	@Override
	protected String extractAccount(SignInDto dto) {
		return dto.account();
	}

	@Override
	protected String extractPassword(SignInDto dto) {
		return dto.password();
	}
}