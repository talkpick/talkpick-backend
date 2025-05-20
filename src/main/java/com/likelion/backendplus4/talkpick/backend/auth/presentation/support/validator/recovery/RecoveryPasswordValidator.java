package com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.recovery;


import static org.springframework.util.StringUtils.*;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.likelion.backendplus4.talkpick.backend.auth.presentation.dto.req.recovery.RecoveryPasswordDto;
import com.likelion.backendplus4.talkpick.backend.auth.presentation.support.validator.enums.AuthValidationError;

@Component
public class RecoveryPasswordValidator implements Validator {

	@Value("#{T(java.util.regex.Pattern).compile('${validator.pattern.email}')}")
	private Pattern EMAIL_PATTERN;

	@Value("#{T(java.util.regex.Pattern).compile('${validator.pattern.password}')}")
	private Pattern PASSWORD_PATTERN;

	@Override
	public boolean supports(Class<?> clazz) {
		return RecoveryPasswordDto.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		RecoveryPasswordDto dto = (RecoveryPasswordDto) target;
		validateEmail(dto.email(), errors);
		validateTempToken(dto.tempToken(), errors);
		validatePassword(dto.password(), errors);
	}

	/**
	 * EMAIL 필드 값의 공통 검증(비어있는지, 공백 포함, 패턴)을 수행합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private void validateEmail(String email, Errors errors) {
		if (rejectIfEmailEmpty(email, errors)) {
			return;
		}
		if (rejectIfEmailWhitespace(email, errors)) {
			return;
		}
		rejectIfEmailPattern(email, errors);
	}


	private void validateTempToken(String tempToken, Errors errors) {
		if (!hasText(tempToken)) {
			AuthValidationError.TEMP_TOKEN_EMPTY.reject(errors);
		}
	}

	/**
	 * password 필드를 종합 검증합니다.
	 *  - null/blank
	 *  - 공백 포함 금지
	 *  - 길이 8~16자
	 *  - 패턴(대문자·소문자·숫자·특수문자 각각 1회 이상)
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private void validatePassword(String password, Errors errors) {
		if (isPasswordEmpty(password, errors)) {
			return;
		}
		if (hasPasswordWhitespace(password, errors)) {
			return;
		}
		if (isPasswordSizeInvalid(password, errors)) {
			return;
		}
		checkPasswordPattern(password, errors);
	}

	/**
	 * EMAIL 값이 비어있는지 확인하고, 비어있으면 오류 등록 후 true를 반환합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private boolean rejectIfEmailEmpty(String email, Errors errors) {
		if (!StringUtils.hasText(email)) {
			AuthValidationError.EMAIL_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * EMAIL 값에 공백이 포함되어 있는지 확인하고, 포함 시 오류 등록 후 true를 반환합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @return 오류 발생 시 true
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private boolean rejectIfEmailWhitespace(String email, Errors errors) {
		if (StringUtils.containsWhitespace(email)) {
			AuthValidationError.EMAIL_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * EMAIL 패턴이 올바른지 확인하고, 올바르지 않으면 오류를 등록합니다.
	 *
	 * @param email  검증할 이메일 문자열
	 * @param errors 오류 정보를 담는 객체
	 * @author 박찬병
	 * @since 2025-05-20
	 */
	private void rejectIfEmailPattern(String email, Errors errors) {
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			AuthValidationError.EMAIL_PATTERN.reject(errors);
		}
	}

	/**
	 * 비밀번호가 null 또는 blank인지 확인하고, 빈 값일 경우 오류를 등록합니다.
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @return 빈 값일 경우 true, 아닐 경우 false
	 * @since 2025-05-20
	 * @author 박찬병
	 */
	private boolean isPasswordEmpty(String password, Errors errors) {
		if (!hasText(password)) {
			AuthValidationError.PASSWORD_EMPTY.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 비밀번호에 공백이 포함되었는지 확인하고, 포함된 경우 오류를 등록합니다.
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @return 공백 포함 시 true, 아닐 시 false
	 * @since 2025-05-20
	 * @author 박찬병
	 */
	private boolean hasPasswordWhitespace(String password, Errors errors) {
		if (containsWhitespace(password)) {
			AuthValidationError.PASSWORD_WHITESPACE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 비밀번호 길이가 8~20자 범위를 벗어났는지 확인하고, 벗어난 경우 오류를 등록합니다.
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @return 크기 범위 벗어날 시 true, 아닐 시 false
	 * @since 2025-05-20
	 * @author 박찬병
	 */
	private boolean isPasswordSizeInvalid(String password, Errors errors) {
		int length = password.length();
		if (7 >= length || length >= 21) {
			AuthValidationError.PASSWORD_SIZE.reject(errors);
			return true;
		}
		return false;
	}

	/**
	 * 비밀번호 패턴(대문자·소문자·숫자·특수문자 각 1회 이상) 매칭을 수행하고, 불일치 시 오류를 등록합니다.
	 *
	 * @param password 비밀번호 문자열
	 * @param errors   Errors
	 * @since 2025-05-20
	 * @author 박찬병
	 */
	private void checkPasswordPattern(String password, Errors errors) {
		if (!PASSWORD_PATTERN.matcher(password).matches()) {
			AuthValidationError.PASSWORD_PATTERN.reject(errors);
		}
	}
}
