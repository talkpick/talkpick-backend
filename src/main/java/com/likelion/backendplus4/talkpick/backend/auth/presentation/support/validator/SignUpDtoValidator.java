package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
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

	/**
	 * name 필드를 검증합니다.
	 *  - null/blank
	 *  - 공백 포함 금지
	 *  - 최대 30자
	 *
	 * @param name   이름 문자열
	 * @param errors Errors
	 */
	private void validateName(String name, Errors errors) {
		if (name == null || name.isBlank()) {
			AuthValidationError.NAME_EMPTY.reject(errors);
			return;
		}
		if (name.contains(" ")) {
			AuthValidationError.NAME_WHITESPACE.reject(errors);
			return;
		}
		if (name.length() > 30) {
			AuthValidationError.NAME_SIZE.reject(errors);
		}
	}

	/**
	 * nickName 필드를 검증합니다.
	 *  - 빈 문자열 검증
	 *  - 공백 포함 금지
	 *  - 최대 20자
	 *
	 * @param nick   닉네임 문자열
	 * @param errors Errors
	 */
	private void validateNickName(String nick, Errors errors) {
		if (nick != null) {
			if (nick.isBlank()) {
				AuthValidationError.NICKNAME_EMPTY.reject(errors);
				return;
			}
			if (nick.contains(" ")) {
				AuthValidationError.NICKNAME_WHITESPACE.reject(errors);
				return;
			}
			if (nick.length() > 20) {
				AuthValidationError.NICKNAME_SIZE.reject(errors);
			}
		}
	}

	/**
	 * email 필드를 검증합니다.
	 *  - null/blank
	 *  - 공백 포함 금지
	 *  - 형식 검증
	 *
	 * @param email  이메일 문자열
	 * @param errors Errors
	 */
	private void validateEmail(String email, Errors errors) {
		if (email == null || email.isBlank()) {
			AuthValidationError.EMAIL_EMPTY.reject(errors);
			return;
		}
		if (email.contains(" ")) {
			AuthValidationError.EMAIL_WHITESPACE.reject(errors);
			return;
		}
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			AuthValidationError.EMAIL_PATTERN.reject(errors);
		}
	}
}