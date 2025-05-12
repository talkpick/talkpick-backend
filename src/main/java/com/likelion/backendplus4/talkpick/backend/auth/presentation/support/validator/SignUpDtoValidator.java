package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

/**
 * SignUpDto에 담긴 회원 가입 요청 정보를 검증하는 Validator.
 * 공통 계정·비밀번호 검증 후 추가 필드를 검사합니다.
 *
 * @author 박찬병
 * @since 2025-05-12
 * @modified 2025-05-12
 */
@Component
public class SignUpDtoValidator extends AbstractAuthValidator<SignUpDto> {

	/** 이메일 패턴: 공백 불가, 로컬파트·서브도메인·TLD 검증 */
	private static final Pattern EMAIL_PATTERN = Pattern.compile(
		"^\\S+@(?:[A-Za-z0-9-]+\\.)+[A-Za-z]{2,6}$"
	);

	public SignUpDtoValidator() {
		super(SignUpDto.class);
	}

	@Override
	protected String extractAccount(SignUpDto dto) {
		return dto.account();
	}

	@Override
	protected String extractPassword(SignUpDto dto) {
		return dto.password();
	}

	@Override
	protected void validateAdditional(SignUpDto dto, Errors errors) {
		validateName(dto.name(), errors);
		validateNickName(dto.nickName(), errors);
		validateEmail(dto.email(), errors);
	}

	private void validateName(String name, Errors errors) {
		if (!StringUtils.hasText(name)) {
			AuthValidationError.NAME_EMPTY.reject(errors);
		} else if (StringUtils.containsWhitespace(name)) {
			AuthValidationError.NAME_WHITESPACE.reject(errors);
		} else if (name.length() > 30) {
			AuthValidationError.NAME_SIZE.reject(errors);
		}
	}

	private void validateNickName(String nick, Errors errors) {
		// 닉네임은 null 허용 → 입력이 있으면 검증
		if (nick != null) {
			if (!StringUtils.hasText(nick)) {
				AuthValidationError.NICKNAME_EMPTY.reject(errors);
			} else if (StringUtils.containsWhitespace(nick)) {
				AuthValidationError.NICKNAME_WHITESPACE.reject(errors);
			} else if (nick.length() > 20) {
				AuthValidationError.NICKNAME_SIZE.reject(errors);
			}
		}
	}

	private void validateEmail(String email, Errors errors) {
		if (!StringUtils.hasText(email)) {
			AuthValidationError.EMAIL_EMPTY.reject(errors);
		} else if (StringUtils.containsWhitespace(email)) {
			AuthValidationError.EMAIL_WHITESPACE.reject(errors);
		} else if (!EMAIL_PATTERN.matcher(email).matches()) {
			AuthValidationError.EMAIL_PATTERN.reject(errors);
		}
	}
}