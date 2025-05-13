package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator;

import java.util.regex.Pattern;
import java.time.LocalDate;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.SignUpDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;
import com.likelion.backendplus4.talkpick.backend.user.infrastructure.adapter.persistence.jpa.entity.Gender;

/**
 * SignUpDto에 담긴 회원 가입 요청 정보를 검증하는 Validator.
 * 공통 계정·비밀번호 검증 후 이름·닉네임·이메일·성별·생년월일을 검증합니다.
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
		validateGender(dto.gender(), errors);
		validateBirthday(dto.birthDay(), errors);
	}

	/**
	 * 이름 값을 검증합니다.
	 *  - null 또는 빈 문자열 불가
	 *  - 공백 문자 불가
	 *  - 최대 30자
	 *
	 * @param name 사용자의 이름
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-13
	 */
	private void validateName(String name, Errors errors) {
		if (!StringUtils.hasText(name)) {
			AuthValidationError.NAME_EMPTY.reject(errors);
		} else if (StringUtils.containsWhitespace(name)) {
			AuthValidationError.NAME_WHITESPACE.reject(errors);
		} else if (name.length() > 30) {
			AuthValidationError.NAME_SIZE.reject(errors);
		}
	}

	/**
	 * 닉네임 값을 검증합니다.
	 *  - null 허용 (입력된 경우에만 검증)
	 *  - null 또는 빈 문자열 불가
	 *  - 공백 문자 불가
	 *  - 최대 20자
	 *
	 * @param nick 사용자의 닉네임
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-13
	 */
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

	/**
	 * 이메일 값을 검증합니다.
	 *  - null 또는 빈 문자열 불가
	 *  - 공백 문자 불가
	 *  - 이메일 형식(로컬파트@도메인) 검증
	 *
	 * @param email 사용자의 이메일
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-13
	 */
	private void validateEmail(String email, Errors errors) {
		if (!StringUtils.hasText(email)) {
			AuthValidationError.EMAIL_EMPTY.reject(errors);
		} else if (StringUtils.containsWhitespace(email)) {
			AuthValidationError.EMAIL_WHITESPACE.reject(errors);
		} else if (!EMAIL_PATTERN.matcher(email).matches()) {
			AuthValidationError.EMAIL_PATTERN.reject(errors);
		}
	}

	/**
	 * 성별 값을 검증합니다.
	 *  - null 허용되지 않음
	 *
	 * @param gender 사용자의 성별
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-13
	 */
	private void validateGender(Gender gender, Errors errors) {
		if (gender == null) {
			AuthValidationError.GENDER_EMPTY.reject(errors);
		}
	}

	/**
	 * 생년월일 값을 검증합니다.
	 *  - null 허용되지 않음
	 *  - 미래 날짜는 허용되지 않음
	 *
	 * @param birthday 사용자의 생년월일
	 * @param errors Errors 객체
	 * @author 박찬병
	 * @since 2025-05-13
	 * @modified 2025-05-13
	 */
	private void validateBirthday(LocalDate birthday, Errors errors) {
		if (birthday == null) {
			AuthValidationError.BIRTHDAY_EMPTY.reject(errors);
		} else if (birthday.isAfter(LocalDate.now())) {
			AuthValidationError.BIRTHDAY_FUTURE.reject(errors);
		}
	}
}